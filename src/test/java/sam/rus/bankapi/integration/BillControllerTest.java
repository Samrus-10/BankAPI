package sam.rus.bankapi.integration;

import sam.rus.bankapi.controller.BillController;
import sam.rus.bankapi.repository.BillRepository;
import sam.rus.bankapi.repository.ConnectionPoll;
import sam.rus.bankapi.repository.Impl.BillRepositoryImpl;
import sam.rus.bankapi.repository.Impl.ConnectionPollImpl;
import sam.rus.bankapi.repository.Impl.UserRepositoryImpl;
import sam.rus.bankapi.repository.UserRepository;
import sam.rus.bankapi.service.Impl.BillServiceImpl;
import sam.rus.bankapi.service.Impl.UserServiceImpl;
import sam.rus.bankapi.util.Authenticator;
import com.sun.net.httpserver.HttpServer;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


import static org.junit.jupiter.api.Assertions.*;


public class BillControllerTest {
    private HttpServer server;
    private final int port = new InetSocketAddress((int) (Math.random() * 65535)).getPort();
    private static final BillRepository billRepository = new BillRepositoryImpl();
    private static final UserRepository userRepository = new UserRepositoryImpl();
    private final UserServiceImpl userService = new UserServiceImpl(userRepository);
    private final BillServiceImpl billService = new BillServiceImpl(billRepository, userService);
    //private static final String url = "jdbc:h2:mem:testIntegration;DB_CLOSE_DELAY=-1";
    private static final String createTable = "src/test/resources/SQLScripts/CreateTestTables.sql";
    private static final String deleteTable = "src/test/resources/SQLScripts/DropTestTables.sql";
    private static final String createAdminUser = "src/test/resources/SQLScripts/CreateAdminUser.sql";
    private static final String createUser = "src/test/resources/SQLScripts/CreateUser.sql";
    private static final String putInformation = "src/test/resources/SQLScripts/putInformation.sql";
    private static Connection connectionDB;
    private static ConnectionPoll connectionPoll = ConnectionPollImpl.getInstance();
    private final BillController billController = new BillController(billService, userService);
    private final Authenticator authenticator = new Authenticator(userService);
    private final String loginPasswordUser1 = "Basic dXNlcjE6MjIyMg==";
    private final String loginPasswordUser2 = "Basic dXNlcjI6MzMzMw==";
    private final String loginPasswordAdmin = "Basic YWRtaW46MTExMQ==";

    @BeforeEach
    void setUp() throws IOException, SQLException {
        MockitoAnnotations.initMocks(this);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        assert server != null;
        server.createContext("/test/bill", billController).setAuthenticator(authenticator);
        server.start();

        connectionDB = connectionPoll.getConnection();
        RunScript.execute(connectionDB, new FileReader(deleteTable));
        RunScript.execute(connectionDB, new FileReader(createTable));
        RunScript.execute(connectionDB, new FileReader(putInformation));
    }

    @AfterEach
    void shutDown() throws SQLException, FileNotFoundException {
        server.stop(0);
        RunScript.execute(connectionDB, new FileReader(deleteTable));
        connectionDB.close();
    }

    @Test
    void handleEmpty() throws IOException, SQLException {
        URL url = new URL("http://localhost:" + port + "/test/bill");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetId() throws IOException, SQLException {
        URL url = new URL("http://localhost:" + port + "/test/bill?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetIdNoAccess() throws IOException, SQLException {
        URL url = new URL("http://localhost:" + port + "/test/bill?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        assertEquals(401, connection.getResponseCode());
    }

    @Test
    void handleGetIdBillNotFound() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/bill?id=4");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetBillId() throws IOException, SQLException {
        URL url = new URL("http://localhost:" + port + "/test/bill?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetBillIdBillNotFound() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/bill?billId=3");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetBillIdBillNoAccess() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/bill?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser2);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleGetAny() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/bill?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }


    @Test
    void handlePostId201() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/bill?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        connection.setDoOutput(true);
        assertEquals(201, connection.getResponseCode());
    }

    @Test
    void handlePostId406() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/bill?id=4");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        connection.setDoOutput(true);
        assertEquals(406, connection.getResponseCode());
    }

    @Test
    void handlePost404() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/bill?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePost403() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/bill");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleAny() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/bill");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(405, connection.getResponseCode());
    }

}
