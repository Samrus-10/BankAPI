package sam.rus.bankapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import sam.rus.bankapi.entity.Card;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.CardNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.service.Impl.CardServiceImpl;
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

class   CardControllerTest {
    @Mock
    private final UserServiceImpl userService = Mockito.mock(UserServiceImpl.class);
    @Mock
    private final CardServiceImpl cardService = Mockito.mock(CardServiceImpl.class);
    @InjectMocks
    private Authenticator authenticator;
    @InjectMocks
    private CardController cardController;
    private HttpServer server;
    private final ObjectMapper mapper = new ObjectMapper();
    private final int port = new InetSocketAddress((int) (Math.random() * 65535)).getPort();

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        assert server != null;
        server.createContext("/test/card", cardController).setAuthenticator(authenticator);
        server.start();
    }

    @AfterEach
    void shutDown() {
        server.stop(0);
    }

    @Test
    void handleGetBillId200() throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card());
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(cardService.getAllCardsByBill(Mockito.anyLong(), Mockito.anyString())).thenReturn(cardList);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetBillId404() throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        List<Card> cardList = new ArrayList<>();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(cardService.getAllCardsByBill(Mockito.anyLong(), Mockito.anyString())).thenReturn(cardList);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGetBillIdNoAccess() throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(cardService.getAllCardsByBill(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(NoAccessException.class);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleUserGetBillIdBillNotFound()
            throws IOException, UserNotFoundException, NoAccessException, BillNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(cardService.getAllCardsByBill(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(BillNotFoundException.class);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleUserGetId200()
            throws IOException, UserNotFoundException, BillNotFoundException, CardNotFoundException, NoAccessException {
        URL url = new URL("http://localhost:" + port + "/test/card?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(cardService.getCardById(Mockito.anyLong(), Mockito.anyString())).thenReturn(new Card());
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleUserGetIdCardNotFound()
            throws IOException, UserNotFoundException, BillNotFoundException, CardNotFoundException, NoAccessException {
        URL url = new URL("http://localhost:" + port + "/test/card?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(cardService.getCardById(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(CardNotFoundException.class);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleUserGetIdBillNotFound()
            throws IOException, UserNotFoundException, BillNotFoundException, CardNotFoundException, NoAccessException {
        URL url = new URL("http://localhost:" + port + "/test/card?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(cardService.getCardById(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(BillNotFoundException.class);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleUserGetIdNoAccess()
            throws IOException, UserNotFoundException, BillNotFoundException, CardNotFoundException, NoAccessException {
        URL url = new URL("http://localhost:" + port + "/test/card?id=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(cardService.getCardById(Mockito.anyLong(),
                Mockito.anyString())).thenThrow(NoAccessException.class);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleUserGetAny() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleEmployeeGetStatus200() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?status=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        List<Card> cardList = new ArrayList<>();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        Mockito.when(cardService.getAllCardsByStatus(Mockito.anyString())).thenReturn(cardList);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleEmployeeGetEmpty200() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        List<Card> cardList = new ArrayList<>();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        Mockito.when(cardService.getAllCards()).thenReturn(cardList);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleEmployeeGetAny() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePostBillId201() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Card card = new Card();
        String jsonRequest = mapper.writeValueAsString(card);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(cardService.addCard(Mockito.anyString(), Mockito.anyLong())).thenReturn(true);
        assertEquals(201, connection.getResponseCode());
    }

    @Test
    void handlePostBillId406() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?billId=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Card card = new Card();
        String jsonRequest = mapper.writeValueAsString(card);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(cardService.addCard(Mockito.anyString(), Mockito.anyLong())).thenReturn(false);
        assertEquals(406, connection.getResponseCode());
    }

    @Test
    void handlePostBillId404() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Card card = new Card();
        String jsonRequest = mapper.writeValueAsString(card);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(cardService.addCard(Mockito.anyString(), Mockito.anyLong())).thenReturn(false);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePost403() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Card card = new Card();
        String jsonRequest = mapper.writeValueAsString(card);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handlePut200() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?id=1&action=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        Mockito.when(cardService.changeCardStatus(Mockito.anyLong(), Mockito.anyString())).thenReturn(true);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handlePut406() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?id=1&action=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        Mockito.when(cardService.changeCardStatus(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        assertEquals(406, connection.getResponseCode());
    }

    @Test
    void handlePut404() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card?qwe=");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handlePut403() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleAny() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/card");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        assertEquals(405, connection.getResponseCode());
    }

}