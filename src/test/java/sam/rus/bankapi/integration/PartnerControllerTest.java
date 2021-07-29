package sam.rus.bankapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import sam.rus.bankapi.controller.PartnerController;
import sam.rus.bankapi.entity.Partner;
import sam.rus.bankapi.repository.ConnectionPoll;
import sam.rus.bankapi.repository.Impl.ConnectionPollImpl;
import sam.rus.bankapi.repository.Impl.PartnerRepositoryImpl;
import sam.rus.bankapi.repository.Impl.UserRepositoryImpl;
import sam.rus.bankapi.repository.PartnerRepository;
import sam.rus.bankapi.repository.UserRepository;
import sam.rus.bankapi.service.Impl.PartnerServiceImpl;
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

public class PartnerControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private HttpServer server;
    private final int port = new InetSocketAddress((int) (Math.random() * 65535)).getPort();
    private static final UserRepository userRepository = new UserRepositoryImpl();
    private static final PartnerRepository partnerRepository = new PartnerRepositoryImpl();
    private final UserServiceImpl userService = new UserServiceImpl(userRepository);
    private final PartnerServiceImpl partnerService = new PartnerServiceImpl(partnerRepository);
    private static final String createTable = "src/test/resources/SQLScripts/CreateTestTables.sql";
    private static final String deleteTable = "src/test/resources/SQLScripts/DropTestTables.sql";
    private static final String createAdminUser = "src/test/resources/SQLScripts/CreateAdminUser.sql";
    private static final String createUser = "src/test/resources/SQLScripts/CreateUser.sql";
    private static final String createUser2 = "src/test/resources/SQLScripts/CreateUser2.sql";
    private static final String putInformation = "src/test/resources/SQLScripts/putInformation.sql";
    private static Connection connectionDB;
    private static ConnectionPoll connectionPoll = ConnectionPollImpl.getInstance();
    private final PartnerController partnerController = new PartnerController(partnerService, userService);
    private final Authenticator authenticator = new Authenticator(userService);
    private final String loginPasswordUser1 = "Basic dXNlcjE6MjIyMg==";
    private final String loginPasswordUser2 = "Basic dXNlcjI6MzMzMw==";
    private final String loginPasswordUser3 = "dXNlcjM6NDQ0NA==";
    private final String loginPasswordAdmin = "Basic YWRtaW46MTExMQ==";

    @BeforeEach
    void setUp() throws IOException, SQLException {
        MockitoAnnotations.initMocks(this);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        assert server != null;
        server.createContext("/test/partner", partnerController).setAuthenticator(authenticator);
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
    void handleGet() throws IOException, SQLException {
        PreparedStatement partner =
                connectionDB.prepareStatement(
                        "INSERT INTO PARTNERS (NAME, PARTNER_BILL) VALUES ( 'K', 1000000000000000002 )");
        partner.execute();
        URL url = new URL("http://localhost:" + port + "/test/partner");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(200, connection.getResponseCode());
    }

//    @Test
//    void handleGetPartnerNotFound() throws IOException {
//        URL url = new URL("http://localhost:" + port + "/test/partner");
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("GET");
//        connection.setRequestProperty("Authorization", loginPasswordUser3);
//        assertEquals(404, connection.getResponseCode());
//    }

    @Test
    void handleGet404() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/partner?qwe=");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGet403() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/partner");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", loginPasswordAdmin);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handlePost201() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/partner");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        connection.setDoOutput(true);
        Partner partner = new Partner("Igor", 1000000000000000001L);
        String jsonRequest = mapper.writeValueAsString(partner);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        assertEquals(201, connection.getResponseCode());
    }


    @Test
    void handlePost404() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/partner?qwe=1");
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
        URL url = new URL("http://localhost:" + port + "/test/partner");
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
        URL url = new URL("http://localhost:" + port + "/test/partner");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", loginPasswordUser1);
        assertEquals(405, connection.getResponseCode());
    }
}
