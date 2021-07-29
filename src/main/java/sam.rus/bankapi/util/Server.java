package sam.rus.bankapi.util;

import com.sun.net.httpserver.HttpServer;
import sam.rus.bankapi.controller.*;
import sam.rus.bankapi.util.exception.StartServerException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    public static void start() throws FileNotFoundException, SQLException, StartServerException {
        PropertiesManager propertiesManager = new PropertiesManager();
        UserController userController = new UserController();
        BillController billController = new BillController();
        CardController cardController = new CardController();
        ReplenishmentController replenishmentController = new ReplenishmentController();
        PartnerController partnerController = new PartnerController();
        OperationController operationController = new OperationController();
        Authenticator authenticator = new Authenticator();
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        int serverPort = propertiesManager.getPort();
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        } catch (IOException e) {
            System.out.println("IO error");
        }
        if(server == null){
            throw new StartServerException();
        }

        server.setExecutor(threadPoolExecutor);

        server.createContext("/user/bills", billController).setAuthenticator(authenticator);
        server.createContext("/user/cards", cardController).setAuthenticator(authenticator);
        server.createContext("/user/balance", billController).setAuthenticator(authenticator);
        server.createContext("/user/replenishment", replenishmentController).setAuthenticator(authenticator);
        server.createContext("/user/partners", partnerController).setAuthenticator(authenticator);
        server.createContext("/user/operations", operationController).setAuthenticator(authenticator);

        server.createContext("/employee/bills", billController).setAuthenticator(authenticator);
        server.createContext("/employee/operations", operationController).setAuthenticator(authenticator);
        server.createContext("/employee/cards", cardController).setAuthenticator(authenticator);
        server.createContext("/employee/users", userController).setAuthenticator(authenticator);

        server.start();
        System.out.println(String.format("Server start on %d port...", serverPort));
    }
}

