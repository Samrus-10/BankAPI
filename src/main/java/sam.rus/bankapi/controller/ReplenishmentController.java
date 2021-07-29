package sam.rus.bankapi.controller;

import sam.rus.bankapi.entity.Replenishment;
import sam.rus.bankapi.util.enums.RequestMethod;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.util.convertor.ReplenishmentConvertor;
import sam.rus.bankapi.service.Impl.ReplenishmentServiceImpl;
import sam.rus.bankapi.service.Impl.UserServiceImpl;
import sam.rus.bankapi.util.QueryParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ReplenishmentController implements HttpHandler {
    private ReplenishmentServiceImpl replenishmentServiceImpl = new ReplenishmentServiceImpl();
    private UserServiceImpl userServiceImpl = new UserServiceImpl();
    private final ReplenishmentConvertor replenishmentMapper = new ReplenishmentConvertor();

    public ReplenishmentController() {
    }

    public ReplenishmentController(ReplenishmentServiceImpl replenishmentServiceImpl, UserServiceImpl userServiceImpl) {
        this.replenishmentServiceImpl = replenishmentServiceImpl;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (RequestMethod.GET.toString().equals(exchange.getRequestMethod())) {
                if (userServiceImpl.getRoleByLogin(exchange.getPrincipal().getUsername()).equals(Role.USER)) {
                    Map<String, String> requestQuery = QueryParser.queryToMap(exchange.getRequestURI().getRawQuery());
                    if (requestQuery.get("billId") != null) {
                        try {
                            List<Replenishment> replenishmentList =
                                    replenishmentServiceImpl.
                                            getAllReplenishmentByBill(Long.parseLong(requestQuery.get("billId")),
                                                    exchange.getPrincipal().getUsername());
                            exchange.sendResponseHeaders(200,
                                    replenishmentMapper.ReplenishmentListToJson(replenishmentList).getBytes().length);
                            OutputStream outputStream = exchange.getResponseBody();
                            outputStream.write(
                                    replenishmentMapper.ReplenishmentListToJson(replenishmentList).getBytes());
                            outputStream.flush();
                            outputStream.close();
                        } catch (BillNotFoundException | UserNotFoundException e) {
                            System.out.println("Bill or user not found");
                            exchange.sendResponseHeaders(404, -1);
                        } catch (NoAccessException e) {
                            System.out.println("No access");
                            exchange.sendResponseHeaders(403, -1);
                        }
                    } else {
                        exchange.sendResponseHeaders(404, -1);
                    }
                } else {
                    exchange.sendResponseHeaders(403, -1);
                }
            } else if (RequestMethod.POST.toString().equals(exchange.getRequestMethod())) {
                if (userServiceImpl.getRoleByLogin(exchange.getPrincipal().getUsername()).equals(Role.USER)) {
                    Map<String, String> requestQuery = QueryParser.queryToMap(exchange.getRequestURI().getRawQuery());
                    if (requestQuery.isEmpty()) {
                        Replenishment replenishment = replenishmentMapper.JsonToReplenishment(exchange);
                        if (replenishmentServiceImpl.addReplenishment(replenishment)) {
                            exchange.sendResponseHeaders(201, -1);
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
