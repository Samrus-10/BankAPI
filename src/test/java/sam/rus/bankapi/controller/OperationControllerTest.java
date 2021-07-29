package sam.rus.bankapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import sam.rus.bankapi.entity.Operation;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.OperationNotFoundException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.service.Impl.OperationServiceImpl;
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

class OperationControllerTest {
    @Mock
    private final UserServiceImpl userService = Mockito.mock(UserServiceImpl.class);
    @Mock
    private final OperationServiceImpl operationService = Mockito.mock(OperationServiceImpl.class);
    @InjectMocks
    private Authenticator authenticator;
    @InjectMocks
    private OperationController operationController;
    private HttpServer server;
    private final ObjectMapper mapper = new ObjectMapper();
    private final int port = new InetSocketAddress((int) (Math.random() * 65535)).getPort();

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        assert server != null;
        server.createContext("/test/operation", operationController).setAuthenticator(authenticator);
        server.start();
    }

    @AfterEach
    void shutDown() {
        server.stop(0);
    }

    @Test
    void handleGetUserBillId()
            throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException, OperationNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        List<Operation> operationList = new ArrayList<>();
        operationList.add(new Operation());
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(operationService.getAllOperationsByBillId(Mockito.anyLong(),
                Mockito.anyString())).thenReturn(operationList);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetUserOperationNotFound()
            throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException,
            OperationNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(operationService.getAllOperationsByBillId(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(OperationNotFoundException.class);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetUserBillNotFound()
            throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException,
            OperationNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(operationService.getAllOperationsByBillId(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(BillNotFoundException.class);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetUserUserNotFound()
            throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException,
            OperationNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(operationService.getAllOperationsByBillId(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(UserNotFoundException.class);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetUserNoAccess()
            throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException,
            OperationNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(operationService.getAllOperationsByBillId(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(NoAccessException.class);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleGetUserAny() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetEmployeeEmpty() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        List<Operation> operationList = new ArrayList<>();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        Mockito.when(operationService.getAllOperations()).thenReturn(operationList);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetEmployeeStatus() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation?status=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        List<Operation> operationList = new ArrayList<>();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        Mockito.when(operationService.getAllOperationsByStatus(Mockito.anyString())).thenReturn(operationList);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetEmployeeAny404() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePost201() throws IOException, UserNotFoundException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Operation operation = new Operation();
        String jsonRequest = mapper.writeValueAsString(operation);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(operationService.addOperation(operation)).thenReturn(true);
        assertEquals(201, connection.getResponseCode());
    }

    @Test
    void handlePost406() throws IOException, UserNotFoundException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Operation operation = new Operation();
        String jsonRequest = mapper.writeValueAsString(operation);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(operationService.addOperation(Mockito.any())).thenReturn(false);
        assertEquals(406, connection.getResponseCode());
    }

    @Test
    void handlePost404() throws IOException, UserNotFoundException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Operation operation = new Operation();
        String jsonRequest = mapper.writeValueAsString(operation);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(operationService.addOperation(operation)).thenThrow(BillNotFoundException.class);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePost403() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Operation operation = new Operation();
        String jsonRequest = mapper.writeValueAsString(operation);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handlePostAny404() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Operation operation = new Operation();
        String jsonRequest = mapper.writeValueAsString(operation);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePUT200() throws IOException, UserNotFoundException, OperationNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation?id=1&action=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        Mockito.when(operationService.changeStatusOperation(Mockito.anyLong(), Mockito.anyString())).thenReturn(true);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handlePUT406() throws IOException, UserNotFoundException, OperationNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation?id=1&action=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        Mockito.when(operationService.changeStatusOperation(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        assertEquals(406, connection.getResponseCode());
    }

    @Test
    void handlePUTOperationNotFound() throws IOException, UserNotFoundException, OperationNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation?id=1&action=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        Mockito.when(operationService.changeStatusOperation(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(OperationNotFoundException.class);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePUT404() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation?qwe=1&action=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleAny() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/operation");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        assertEquals(405, connection.getResponseCode());
    }

}