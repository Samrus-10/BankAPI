package sam.rus.bankapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import sam.rus.bankapi.controller.OperationController;
import sam.rus.bankapi.entity.Operation;
import sam.rus.bankapi.repository.BillRepository;
import sam.rus.bankapi.repository.ConnectionPoll;
import sam.rus.bankapi.repository.Impl.BillRepositoryImpl;
import sam.rus.bankapi.repository.Impl.ConnectionPollImpl;
import sam.rus.bankapi.repository.Impl.OperationRepositoryImpl;
import sam.rus.bankapi.repository.Impl.UserRepositoryImpl;
import sam.rus.bankapi.repository.OperationRepository;
import sam.rus.bankapi.repository.UserRepository;
import sam.rus.bankapi.service.Impl.BillServiceImpl;
import sam.rus.bankapi.service.Impl.OperationServiceImpl;
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

public class OperationControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private HttpServer server;
    private final int port = new InetSocketAddress((int) (Math.random() * 65535)).getPort();
    private static final BillRepository billRepository = new BillRepositoryImpl();
    private static final UserRepository userRepository = new UserRepositoryImpl();
    private static final OperationRepository operationRepository = new OperationRepositoryImpl();
    private final UserServiceImpl userService = new UserServiceImpl(userRepository);
    private final BillServiceImpl billService = new BillServiceImpl(billRepository, userService);
    private final OperationServiceImpl operationService =
            new OperationServiceImpl(operationRepository, billService, userService);
    private static final String url = "jdbc:h2:mem:testIntegration;DB_CLOSE_DELAY=-1";
    private static final String createTable = "src/test/resources/SQLScripts/CreateTestTables.sql";
    private static final String deleteTable = "src/test/resources/SQLScripts/DropTestTables.sql";
    private static final String createAdminUser = "src/test/resources/SQLScripts/CreateAdminUser.sql";
    private static final String createUser = "src/test/resources/SQLScripts/CreateUser.sql";
    private static final String createUser2 = "src/test/resources/SQLScripts/CreateUser2.sql";
    private static final String putInformation = "src/test/resources/SQLScripts/putInformation.sql";
    private static Connection connectionDB;
    private static ConnectionPoll connectionPoll = ConnectionPollImpl.getInstance();
    private final OperationController operationController = new OperationController(operationService, userService);
    private final Authenticator authenticator = new Authenticator(userService);
    private static final String userNotFound = "Basic cXdlcnR5OmFkbWlu";
    private final String loginPasswordUser1 = "Basic dXNlcjE6MjIyMg==";
    private final String loginPasswordUser2 = "Basic dXNlcjI6MzMzMw==";
    private final String loginPasswordAdmin = "Basic YWRtaW46MTExMQ==";

    @BeforeEach
    void setUp() throws IOException, SQLException {
        MockitoAnnotations.initMocks(this);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        assert server != null;
        server.createContext("/test/operation", operationController).setAuthenticator(authenticator);
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
    void handleGetUserBillId() throws IOException, SQLException {
        PreparedStatement addBill = connectionDB.prepareStatement("INSERT INTO BILLS(USER_ID) VALUES (2)");
        addBill.execute();
        PreparedStatement partner =
                connectionDB.prepareStatement(
                        "INSERT INTO PARTNERS (NAME, PARTNER_BILL) VALUES ( 'K', 1000000000000000002 )");
        partner.execute();
        PreparedStatement operation1 =
                connectionDB.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 1, 1, 100 )");
        PreparedStatement operation2 =
                connectionDB.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 1, 1, 200 )");
        operation1.execute();
        operation2.execute();
        URL url = new URL("http://localhost:" + port + "/test/operation?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetUserOperationNotFound() throws IOException, SQLException {
        PreparedStatement addBill = connectionDB.prepareStatement("INSERT INTO BILLS(USER_ID) VALUES (2)");
        addBill.execute();
        URL url = new URL("http://localhost:" + port + "/test/operation?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetUserBillNotFound() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/operation?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetUserUserNotFound() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/operation?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", userNotFound);
        assertEquals(401, connection.getResponseCode());
    }

    @Test
    void handleGetUserNoAccess() throws IOException, SQLException {
        PreparedStatement addBill = connectionDB.prepareStatement("INSERT INTO BILLS(USER_ID) VALUES (2)");
        addBill.execute();
        PreparedStatement partner =
                connectionDB.prepareStatement(
                        "INSERT INTO PARTNERS (NAME, PARTNER_BILL) VALUES ( 'K', 1000000000000000002 )");
        partner.execute();
        PreparedStatement operation1 =
                connectionDB.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 1, 1, 100 )");
        PreparedStatement operation2 =
                connectionDB.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 1, 1, 200 )");
        operation1.execute();
        operation2.execute();
        URL url = new URL("http://localhost:" + port + "/test/operation?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser2);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleGetUserAny() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/operation?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetEmployeeEmpty() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/operation");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetEmployeeStatus() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/operation?status=active");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetEmployeeAny404() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/operation?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePost201() throws IOException, SQLException {
//        PreparedStatement addBill = connectionDB.prepareStatement(
//                "INSERT INTO BILLS(USER_ID, BALANCE) VALUES (3, 1000)");
        //addBill.execute();
        PreparedStatement partner =
                connectionDB.prepareStatement(
                        "INSERT INTO PARTNERS (NAME, PARTNER_BILL) VALUES ( 'K', 1000000000000000002 )");
        partner.execute();
        URL url = new URL("http://localhost:" + port + "/test/operation");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        connection.setDoOutput(true);
        Operation operation = new Operation(1, 1, 100);
        String jsonRequest = mapper.writeValueAsString(operation);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        assertEquals(201, connection.getResponseCode());
    }

    @Test
    void handlePost406() throws IOException, SQLException {
        PreparedStatement addBill = connectionDB.prepareStatement(
                "INSERT INTO BILLS(USER_ID, BALANCE) VALUES (2, 1000)");
        addBill.execute();
        PreparedStatement partner =
                connectionDB.prepareStatement(
                        "INSERT INTO PARTNERS (NAME, PARTNER_BILL) VALUES ( 'K', 1000000000000000002 )");
        partner.execute();
        URL url = new URL("http://localhost:" + port + "/test/operation");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        connection.setDoOutput(true);
        Operation operation = new Operation(1, 2, 100000);
        String jsonRequest = mapper.writeValueAsString(operation);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        assertEquals(406, connection.getResponseCode());
    }

    @Test
    void handlePost403() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/operation");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        connection.setDoOutput(true);
        Operation operation = new Operation();
        String jsonRequest = mapper.writeValueAsString(operation);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handlePostAny404() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/operation?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        connection.setDoOutput(true);
        Operation operation = new Operation(1, 1, 100);
        String jsonRequest = mapper.writeValueAsString(operation);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePUT200() throws IOException, SQLException {
        PreparedStatement addBill = connectionDB.prepareStatement(
                "INSERT INTO BILLS(USER_ID, BALANCE) VALUES (2, 1000)");
        addBill.execute();
        PreparedStatement partner =
                connectionDB.prepareStatement(
                        "INSERT INTO PARTNERS (NAME, PARTNER_BILL) VALUES ( 'K', 1000000000000000002 )");
        partner.execute();
        PreparedStatement operation =
                connectionDB.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 1, 1, 100 )");
        operation.execute();
        URL url = new URL("http://localhost:" + port + "/test/operation?id=1&action=approved");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handlePUT406() throws IOException, SQLException {
        PreparedStatement addBill = connectionDB.prepareStatement(
                "INSERT INTO BILLS(USER_ID, BALANCE) VALUES (2, 1000)");
        addBill.execute();
        PreparedStatement partner =
                connectionDB.prepareStatement(
                        "INSERT INTO PARTNERS (NAME, PARTNER_BILL) VALUES ( 'K', 1000000000000000002 )");
        partner.execute();
        PreparedStatement operation =
                connectionDB.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 1, 1, 100 )");
        operation.execute();
        URL url = new URL("http://localhost:" + port + "/test/operation?id=1&action=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(406, connection.getResponseCode());
    }

    @Test
    void handlePUTOperationNotFound() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/operation?id=1&action=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePUT404() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/operation?qwe=1&action=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleAny() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/operation");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(405, connection.getResponseCode());
    }

}
