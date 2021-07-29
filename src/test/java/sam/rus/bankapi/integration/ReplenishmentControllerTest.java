package sam.rus.bankapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import sam.rus.bankapi.controller.ReplenishmentController;
import sam.rus.bankapi.entity.Replenishment;
import sam.rus.bankapi.repository.BillRepository;
import sam.rus.bankapi.repository.ConnectionPoll;
import sam.rus.bankapi.repository.Impl.BillRepositoryImpl;
import sam.rus.bankapi.repository.Impl.ConnectionPollImpl;
import sam.rus.bankapi.repository.Impl.ReplenishmentRepositoryImpl;
import sam.rus.bankapi.repository.Impl.UserRepositoryImpl;
import sam.rus.bankapi.repository.ReplenishmentRepository;
import sam.rus.bankapi.repository.UserRepository;
import sam.rus.bankapi.service.Impl.BillServiceImpl;
import sam.rus.bankapi.service.Impl.ReplenishmentServiceImpl;
import sam.rus.bankapi.service.Impl.UserServiceImpl;
import sam.rus.bankapi.util.Authenticator;
import com.sun.net.httpserver.HttpServer;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.io.DataOutputStream;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReplenishmentControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private HttpServer server;
    private final int port = new InetSocketAddress((int) (Math.random() * 65535)).getPort();
    private static final BillRepository billRepository = new BillRepositoryImpl();
    private static final UserRepository userRepository = new UserRepositoryImpl();
    private static final ReplenishmentRepository replenishmentRepository = new ReplenishmentRepositoryImpl();
    private final UserServiceImpl userService = new UserServiceImpl(userRepository);
    private final BillServiceImpl billService = new BillServiceImpl(billRepository, userService);
    private final ReplenishmentServiceImpl replenishmentService =
            new ReplenishmentServiceImpl(replenishmentRepository, billService, userService);
    private static final String createTable = "src/test/resources/SQLScripts/CreateTestTables.sql";
    private static final String deleteTable = "src/test/resources/SQLScripts/DropTestTables.sql";
    private static final String putInformation = "src/test/resources/SQLScripts/putInformation.sql";
    private static Connection connectionDB;
        private static ConnectionPoll connectionPoll = ConnectionPollImpl.getInstance();
    private final ReplenishmentController replenishmentController =
            new ReplenishmentController(replenishmentService, userService);
    private final Authenticator authenticator = new Authenticator(userService);
    private final String loginPasswordUser1 = "Basic dXNlcjE6MjIyMg==";
    private final String loginPasswordUser2 = "Basic dXNlcjI6MzMzMw==";
    private final String loginPasswordUser3 = "dXNlcjM6NDQ0NA==";
    private final String loginPasswordAdmin = "Basic YWRtaW46MTExMQ==";
    private static final String userNotFound = "Basic cXdlcnR5OmFkbWlu";

    @BeforeEach
    void setUp() throws IOException, SQLException {
        MockitoAnnotations.initMocks(this);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        assert server != null;
        server.createContext("/test/replenishment", replenishmentController).setAuthenticator(authenticator);
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
    void handleGet200() throws IOException, SQLException {
        PreparedStatement addBill = connectionDB.prepareStatement("INSERT INTO BILLS(USER_ID) VALUES (2)");
        addBill.execute();
        PreparedStatement replenishment1 =
                connectionDB.prepareStatement("INSERT INTO REPLENISHMENTS (SUM, BILL_ID) VALUES ( 100, 1 )");
        PreparedStatement replenishment2 =
                connectionDB.prepareStatement("INSERT INTO REPLENISHMENTS (SUM, BILL_ID) VALUES ( 200, 1 )");
        replenishment1.execute();
        replenishment2.execute();
        URL url = new URL("http://localhost:" + port + "/test/replenishment?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetBillNotFound() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment?billId=4");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetUserNotFound() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", userNotFound);
        assertEquals(401, connection.getResponseCode());
    }

    @Test
    void handleGetNoAccess() throws IOException, SQLException {
        PreparedStatement addBill = connectionDB.prepareStatement("INSERT INTO BILLS(USER_ID) VALUES (2)");
        addBill.execute();
        PreparedStatement replenishment1 =
                connectionDB.prepareStatement("INSERT INTO REPLENISHMENTS (SUM, BILL_ID) VALUES ( 100, 1 )");
        PreparedStatement replenishment2 =
                connectionDB.prepareStatement("INSERT INTO REPLENISHMENTS (SUM, BILL_ID) VALUES ( 200, 1 )");
        replenishment1.execute();
        replenishment2.execute();
        URL url = new URL("http://localhost:" + port + "/test/replenishment?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser2);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleGet404() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGet403() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handlePost201() throws IOException, SQLException {
        PreparedStatement addBill = connectionDB.prepareStatement("INSERT INTO BILLS(USER_ID) VALUES (2)");
        addBill.execute();
        URL url = new URL("http://localhost:" + port + "/test/replenishment");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        connection.setDoOutput(true);
        Replenishment replenishment = new Replenishment(100, 1);
        String jsonRequest = mapper.writeValueAsString(replenishment);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        assertEquals(201, connection.getResponseCode());
    }



    @Test
    void handlePost404() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        connection.setDoOutput(true);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePost403() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        connection.setDoOutput(true);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleAny() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(405, connection.getResponseCode());
    }

}
