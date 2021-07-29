package sam.rus.bankapi.controller;

import sam.rus.bankapi.entity.User;
import sam.rus.bankapi.util.enums.RequestMethod;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.util.convertor.UserConvertor;
import sam.rus.bankapi.service.Impl.UserServiceImpl;
import sam.rus.bankapi.service.UserService;
import sam.rus.bankapi.util.QueryParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class UserController implements HttpHandler {
    private UserService userServiceImpl = new UserServiceImpl();
    private final UserConvertor userMapper = new UserConvertor();

    public UserController() {
    }

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (RequestMethod.POST.toString().equals(exchange.getRequestMethod())) {
                if (userServiceImpl.getRoleByLogin(exchange.getPrincipal().getUsername()).equals(Role.EMPLOYEE)) {
                    Map<String, String> requestQuery = QueryParser.queryToMap(exchange.getRequestURI().getRawQuery());
                    if (requestQuery.isEmpty()) {
                        User user = userMapper.JsonToUser(exchange);
                        if (userServiceImpl.addUser(user)) {
                            exchange.sendResponseHeaders(201, -1);
                            exchange.close();
                        } else {
                            exchange.sendResponseHeaders(406, -1);
                        }
                    } else {
                        exchange.sendResponseHeaders(404, -1);
                    }
                } else {
                    exchange.sendResponseHeaders(403, -1);
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        } catch (IOException e) {
            System.out.println("IO error");
        } catch (UserNotFoundException e) {
            System.out.println("User not found");
        }
    }
}

