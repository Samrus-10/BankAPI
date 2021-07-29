package sam.rus.bankapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import sam.rus.bankapi.entity.Partner;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.PartnerNotFoundException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.service.Impl.PartnerServiceImpl;
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

class PartnerControllerTest {
    @Mock
    private final UserServiceImpl userService = Mockito.mock(UserServiceImpl.class);
    @Mock
    private final PartnerServiceImpl partnerService = Mockito.mock(PartnerServiceImpl.class);
    @InjectMocks
    private Authenticator authenticator;
    @InjectMocks
    private PartnerController partnerController;
    private HttpServer server;
    private final ObjectMapper mapper = new ObjectMapper();
    private final int port = new InetSocketAddress((int) (Math.random() * 65535)).getPort();

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        assert server != null;
        server.createContext("/test/partner", partnerController).setAuthenticator(authenticator);
        server.start();
    }

    @AfterEach
    void shutDown() {
        server.stop(0);
    }

    @Test
    void handleGet() throws IOException, UserNotFoundException, PartnerNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/partner");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        List<Partner> partnerList = new ArrayList<>();
        partnerList.add(new Partner());
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(partnerService.getAllPartners()).thenReturn(partnerList);
        assertEquals(200, connection.getResponseCode());
    }

    @Test
    void handleGetPartnerNotFound() throws IOException, UserNotFoundException, PartnerNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/partner");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(partnerService.getAllPartners()).thenThrow(PartnerNotFoundException.class);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGet404() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/partner?qwe=");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        assertEquals(404, connection.getResponseCode());
    }

    @Test
    void handleGet403() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/partner");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handlePost201() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/partner");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Partner partner = new Partner();
        String jsonRequest = mapper.writeValueAsString(partner);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(partnerService.addPartner(Mockito.any())).thenReturn(true);
        assertEquals(201, connection.getResponseCode());
    }

    @Test
    void handlePost406() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/partner");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Partner partner = new Partner();
        String jsonRequest = mapper.writeValueAsString(partner);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        Mockito.when(partnerService.addPartner(Mockito.any())).thenReturn(false);
        assertEquals(406, connection.getResponseCode());
    }

    @Test
    void handlePost404() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/partner?qwe=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Partner partner = new Partner();
        String jsonRequest = mapper.writeValueAsString(partner);
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
        URL url = new URL("http://localhost:" + port + "/test/partner");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        connection.setDoOutput(true);
        Partner partner = new Partner();
        String jsonRequest = mapper.writeValueAsString(partner);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(jsonRequest);
        out.flush();
        out.close();
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.EMPLOYEE);
        Mockito.when(partnerService.addPartner(Mockito.any())).thenReturn(true);
        assertEquals(403, connection.getResponseCode());
    }

    @Test
    void handleAny() throws IOException, UserNotFoundException {
        URL url = new URL("http://localhost:" + port + "/test/partner");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Authorization", "Basic MTIzOjEyMw==");
        Mockito.when(userService.authentication(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        Mockito.when(userService.getRoleByLogin(Mockito.anyString())).thenReturn(Role.USER);
        assertEquals(405, connection.getResponseCode());
    }
}