package sam.rus.bankapi.integration;

import sam.rus.bankapi.controller.CardController;
import sam.rus.bankapi.repository.BillRepository;
import sam.rus.bankapi.repository.CardRepository;
import sam.rus.bankapi.repository.ConnectionPoll;
import sam.rus.bankapi.repository.Impl.BillRepositoryImpl;
import sam.rus.bankapi.repository.Impl.CardRepositoryImpl;
import sam.rus.bankapi.repository.Impl.ConnectionPollImpl;
import sam.rus.bankapi.repository.Impl.UserRepositoryImpl;
import sam.rus.bankapi.repository.UserRepository;
import sam.rus.bankapi.service.Impl.BillServiceImpl;
import sam.rus.bankapi.service.Impl.CardServiceImpl;
import sam.rus.bankapi.service.Impl.UserServiceImpl;
import sam.rus.bankapi.util.Authenticator;
import com.sun.net.httpserver.HttpServer;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardControllerTest {
    private HttpServer server;
    private final int port = new InetSocketAddress((int) (Math.random() * 65535)).getPort();
    private static final BillRepository billRepository = new BillRepositoryImpl();
    private static final UserRepository userRepository = new UserRepositoryImpl();
    private static final CardRepository cardRepository = new CardRepositoryImpl();
    private final UserServiceImpl userService = new UserServiceImpl(userRepository);
    private final BillServiceImpl billService = new BillServiceImpl(billRepository, userService);
    private final CardServiceImpl cardService = new CardServiceImpl(cardRepository, userService, billService);
    private static final String createTable = "src/test/resources/SQLScripts/CreateTestTables.sql";
    private static final String deleteTable = "src/test/resources/SQLScripts/DropTestTables.sql";
    private static final String putInformation = "src/test/resources/SQLScripts/putInformation.sql";
    private static Connection connectionDB;
    private static ConnectionPoll connectionPoll = ConnectionPollImpl.getInstance();
    private final CardController cardController = new CardController(userService, cardService);
    private final Authenticator authenticator = new Authenticator(userService);
    private final String loginPasswordUser1 = "Basic dXNlcjE6MjIyMg==";
    private final String loginPasswordUser2 = "Basic dXNlcjI6MzMzMw==";
    private final String loginPasswordAdmin = "Basic YWRtaW46MTExMQ==";

    @BeforeEach
    void setUp() throws IOException, SQLException {
        MockitoAnnotations.initMocks(this);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        assert server != null;
        server.createContext("/test/card", cardController).setAuthenticator(authenticator);
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
    void handleGetBillId200() throws IOException, SQLException {
        URL url = new URL("http://localhost:" + port + "/test/card?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetBillId404() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/card?billId=4");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetBillIdNoAccess() throws IOException, SQLException {
        URL url = new URL("http://localhost:" + port + "/test/card?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser2);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleUserGetBillIdBillNotFound() throws IOException, SQLException {
        URL url = new URL("http://localhost:" + port + "/test/card?billId=3");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleUserGetId200() throws IOException, SQLException {
        URL url = new URL("http://localhost:" + port + "/test/card?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleUserGetIdCardNotFound() throws IOException, SQLException {
        URL url = new URL("http://localhost:" + port + "/test/card?id=4");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleUserGetIdBillNotFound() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/card?id=4");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleUserGetIdNoAccess() throws IOException, SQLException {
        URL url = new URL("http://localhost:" + port + "/test/card?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser2);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleUserGetAny() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/card?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleEmployeeGetStatus200() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/card?status=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleEmployeeGetEmpty200() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/card");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleEmployeeGetAny() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/card?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePostBillId201() throws IOException, SQLException {
        URL url = new URL("http://localhost:" + port + "/test/card?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        connection.setDoOutput(true);
        assertEquals(201, connection.getResponseCode());
    }

    @Test
    void handlePostBillId406() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/card?billId=5");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        connection.setDoOutput(true);
        assertEquals(406, connection.getResponseCode());
    }

    @Test
    void handlePostBillId404() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/card?qwe=1");
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
        URL url = new URL("http://localhost:" + port + "/test/card?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        connection.setDoOutput(true);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handlePut200() throws IOException, SQLException {
        URL url = new URL("http://localhost:" + port + "/test/card?id=1&action=active");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handlePut406() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/card?id=1&action=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(406, connection.getResponseCode());
    }

    @Test
    void handlePut404() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/card?qwe=");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePut403() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/card");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleAny() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/card");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(405, connection.getResponseCode());
    }

}
