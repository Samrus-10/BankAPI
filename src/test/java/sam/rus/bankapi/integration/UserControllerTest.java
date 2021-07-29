package sam.rus.bankapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import sam.rus.bankapi.controller.UserController;
import sam.rus.bankapi.entity.User;
import sam.rus.bankapi.repository.ConnectionPoll;
import sam.rus.bankapi.repository.Impl.ConnectionPollImpl;
import sam.rus.bankapi.repository.Impl.UserRepositoryImpl;
import sam.rus.bankapi.repository.UserRepository;
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
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private HttpServer server;
    private final int port = new InetSocketAddress((int) (Math.random() * 65535)).getPort();
    private static final UserRepository userRepository = new UserRepositoryImpl();
    private final UserServiceImpl userService = new UserServiceImpl(userRepository);
    private static final String url = "jdbc:h2:mem:testIntegration;DB_CLOSE_DELAY=-1";
    private static final String createTable = "src/test/resources/SQLScripts/CreateTestTables.sql";
    private static final String deleteTable = "src/test/resources/SQLScripts/DropTestTables.sql";
    private static final String createAdminUser = "src/test/resources/SQLScripts/CreateAdminUser.sql";
    private static final String createUser = "src/test/resources/SQLScripts/CreateUser.sql";
    private static final String createUser2 = "src/test/resources/SQLScripts/CreateUser2.sql";
    private static final String putInformation = "src/test/resources/SQLScripts/putInformation.sql";
    private static Connection connectionDB;
    private static ConnectionPoll connectionPoll = ConnectionPollImpl.getInstance();
    private final UserController userController = new UserController(userService);
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
        server.createContext("/test/user", userController).setAuthenticator(authenticator);
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
    void handle406() throws IOException {
        URL url = new URL("http://localhost:"+port+"/test/user");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        connection.setDoOutput(true);
        User user = new User();
        String jsonRequest = mapper.writeValueAsString(user);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        assertEquals(406, connection.getResponseCode());
    }

    @Test
    void handle404() throws IOException {
        URL url = new URL("http://localhost:"+port+"/test/user?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        connection.setDoOutput(true);
        User user = new User();
        String jsonRequest = mapper.writeValueAsString(user);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        assertEquals(404, connection.getResponseCode());
    }
    @Test
    void handle403() throws IOException {
        URL url = new URL("http://localhost:"+port+"/test/user");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        connection.setDoOutput(true);
        User user = new User("123", "123", "V",
                "K", "A", "123", "123", null);
        String jsonRequest = mapper.writeValueAsString(user);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handle405() throws IOException {
        URL url = new URL("http://localhost:"+port+"/test/user");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(405, connection.getResponseCode());
    }
}
