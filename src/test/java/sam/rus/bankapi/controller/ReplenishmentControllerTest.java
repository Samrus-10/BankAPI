package sam.rus.bankapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import sam.rus.bankapi.entity.Replenishment;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.service.Impl.ReplenishmentServiceImpl;
import sam.rus.bankapi.service.Impl.UserServiceImpl;
import sam.rus.bankapi.util.Authenticator;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReplenishmentControllerTest {
    @Mock
    private final UserServiceImpl userService = Mockito.mock(UserServiceImpl.class);
    @Mock
    private final ReplenishmentServiceImpl replenishmentService = Mockito.mock(ReplenishmentServiceImpl.class);
    @InjectMocks
    private Authenticator authenticator;
    @InjectMocks
    ReplenishmentController replenishmentController;
    private HttpServer server;
    private final ObjectMapper mapper = new ObjectMapper();
    private final int port = new InetSocketAddress((int) (Math.random() * 65535)).getPort();

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        assert server != null;
        server.createContext("/test/replenishment", replenishmentController).setAuthenticator(authenticator);
        server.start();
    }

    @AfterEach
    void shutDown() {
        server.stop(0);
    }

    @Test
    void handleGet200() throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        List<Replenishment> replenishmentList = new ArrayList<>();
        replenishmentList.add(new Replenishment());
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(replenishmentService.getAllReplenishmentByBill(Mockito.anyLong(),
                Mockito.anyString())).thenReturn(replenishmentList);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetBillNotFound() throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(replenishmentService.getAllReplenishmentByBill(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(BillNotFoundException.class);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetUserNotFound() throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(replenishmentService.getAllReplenishmentByBill(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(UserNotFoundException.class);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetNoAccess() throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(replenishmentService.getAllReplenishmentByBill(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(NoAccessException.class);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleGet404() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGet403() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handlePost201() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Replenishment replenishment = new Replenishment();
        String jsonRequest = mapper.writeValueAsString(replenishment);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(replenishmentService.addReplenishment(Mockito.any())).thenReturn(true);
        assertEquals(201, connection.getResponseCode());
    }

    @Test
    void handlePost406() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Replenishment replenishment = new Replenishment();
        String jsonRequest = mapper.writeValueAsString(replenishment);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(replenishmentService.addReplenishment(Mockito.any())).thenReturn(false);
        assertEquals(406, connection.getResponseCode());
    }

    @Test
    void handlePost404() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Replenishment replenishment = new Replenishment();
        String jsonRequest = mapper.writeValueAsString(replenishment);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePost403() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Replenishment replenishment = new Replenishment();
        String jsonRequest = mapper.writeValueAsString(replenishment);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleAny() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/replenishment");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        assertEquals(405, connection.getResponseCode());
    }
}