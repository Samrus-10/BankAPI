package sam.rus.bankapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import sam.rus.bankapi.entity.Bill;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.service.Impl.BillServiceImpl;
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
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BillControllerTest {
    @Mock
    private final UserServiceImpl userService = Mockito.mock(UserServiceImpl.class);
    @Mock
    private final BillServiceImpl billService = Mockito.mock(BillServiceImpl.class);
    @InjectMocks
    private Authenticator authenticator;
    @InjectMocks
    private BillController billController;
    private HttpServer server;
    private final ObjectMapper mapper = new ObjectMapper();
    private final int port = new InetSocketAddress((int) (Math.random() * 65535)).getPort();

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        assert server != null;
        server.createContext("/test/bill", billController).setAuthenticator(authenticator);
        server.start();
    }

    @AfterEach
    void shutDown() {
        server.stop(0);
    }

    @Test
    void handleEmpty() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/bill");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        List<Bill> billList = new ArrayList<>();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(userService.getUserIdByLogin(Mockito.anyString())).thenReturn((long) 1);
        Mockito.when(billService.getAllBillsByUser(Mockito.anyInt())).thenReturn(billList);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetId() throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/bill?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Bill bill = new Bill();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(billService.getBillByIdAndLogin(Mockito.anyLong(), Mockito.anyString())).thenReturn(bill);
        assertEquals(connection.getResponseCode(), 200);
    }



    @Test
    void handleGetIdBillNotFound() throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/bill?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(billService.getBillByIdAndLogin(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(BillNotFoundException.class);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetBillId() throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/bill?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(billService.getBalance(Mockito.anyLong(), Mockito.anyString())).thenReturn((double) 0);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetBillIdBillNotFound()
            throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/bill?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(billService.getBalance(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(BillNotFoundException.class);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetBillIdBillNoAccess()
            throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/bill?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(billService.getBalance(Mockito.anyLong(), Mockito.anyString())).thenThrow(NoAccessException.class);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleGetAny() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/bill?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGet403() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/bill?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handlePostId201() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/bill?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Bill bill = new Bill();
        String jsonRequest = mapper.writeValueAsString(bill);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        Mockito.when(billService.addBill(Mockito.anyLong())).thenReturn(true);
        assertEquals(201, connection.getResponseCode());
    }

    @Test
    void handlePostId406() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/bill?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Bill bill = new Bill();
        String jsonRequest = mapper.writeValueAsString(bill);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        Mockito.when(billService.addBill(Mockito.anyLong())).thenReturn(false);
        assertEquals(406, connection.getResponseCode());
    }

    @Test
    void handlePost404() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/bill?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePost403() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/bill");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleAny() throws IOException {
        URL url = new URL("http://localhost:" + port + "/test/bill");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        assertEquals(405, connection.getResponseCode());
    }
}