package sam.rus.bankapi.controller;

import sam.rus.bankapi.entity.Operation;
import sam.rus.bankapi.util.enums.RequestMethod;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.OperationNotFoundException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.util.convertor.OperationConvertor;
import sam.rus.bankapi.service.Impl.OperationServiceImpl;
import sam.rus.bankapi.service.Impl.UserServiceImpl;
import sam.rus.bankapi.util.QueryParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class OperationController implements HttpHandler {
    private OperationServiceImpl operationServiceImpl = new OperationServiceImpl();
    private UserServiceImpl userServiceImpl = new UserServiceImpl();
    private final OperationConvertor operationMapper = new OperationConvertor();

    public OperationController() {
    }

    public OperationController(OperationServiceImpl operationServiceImpl, UserServiceImpl userServiceImpl) {
        this.operationServiceImpl = operationServiceImpl;
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
                            List<Operation> operationList =
                                    operationServiceImpl.getAllOperationsByBillId(
                                            Long.parseLong(requestQuery.get("billId")),
                                            exchange.getPrincipal().getUsername());
                            exchange.sendResponseHeaders(200,
                                    operationMapper.OperationListToJson(operationList).getBytes().length);
                            OutputStream outputStream = exchange.getResponseBody();
                            outputStream.write(operationMapper.OperationListToJson(operationList).getBytes());
                            outputStream.flush();
                            outputStream.close();
                        } catch (OperationNotFoundException | BillNotFoundException | UserNotFoundException e) {
                            System.out.println("Operation, bill or user not found");
                            exchange.sendResponseHeaders(404, -1);
                        } catch (NoAccessException e) {
                            System.out.println("No access");
                            exchange.sendResponseHeaders(403, -1);
                        }
                    } else {
                        exchange.sendResponseHeaders(404, -1);
                    }
                } else if (userServiceImpl.getRoleByLogin(exchange.getPrincipal().getUsername()).equals(Role.EMPLOYEE)) {
                    Map<String, String> requestQuery = QueryParser.queryToMap(exchange.getRequestURI().getRawQuery());
                    if (requestQuery.isEmpty()) {
                        List<Operation> operationList = operationServiceImpl.getAllOperations();
                        exchange.sendResponseHeaders(200,
                                operationMapper.OperationListToJson(operationList).getBytes().length);
                        OutputStream outputStream = exchange.getResponseBody();
                        outputStream.write(operationMapper.OperationListToJson(operationList).getBytes());
                        outputStream.flush();
                        outputStream.close();
                    } else if (requestQuery.get("status") != null) {
                        List<Operation> operationList =
                                operationServiceImpl.getAllOperationsByStatus(requestQuery.get("status"));
                        exchange.sendResponseHeaders(200,
                                operationMapper.OperationListToJson(operationList).getBytes().length);
                        OutputStream outputStream = exchange.getResponseBody();
                        outputStream.write(operationMapper.OperationListToJson(operationList).getBytes());
                        outputStream.flush();
                        outputStream.close();
                    } else {
                        exchange.sendResponseHeaders(404, -1);
                    }
                }
            } else if (RequestMethod.POST.toString().equals(exchange.getRequestMethod())) {
                if (userServiceImpl.getRoleByLogin(exchange.getPrincipal().getUsername()).equals(Role.USER)) {
                    Map<String, String> requestQuery = QueryParser.queryToMap(exchange.getRequestURI().getRawQuery());
                    if (requestQuery.isEmpty()) {
                        try {
                            Operation operation = operationMapper.JsonToOperation(exchange);
                            if (operationServiceImpl.addOperation(operation)) {
                                exchange.sendResponseHeaders(201, -1);
                            } else {
                                exchange.sendResponseHeaders(406, -1);
                            }
                        } catch (BillNotFoundException e) {
                            System.out.println("Bill not found");
                            exchange.sendResponseHeaders(404, -1);
                        }
                    } else {
                        exchange.sendResponseHeaders(404, -1);
                    }
                } else {
                    exchange.sendResponseHeaders(403, -1);
                }
            } else if (RequestMethod.PUT.toString().equals(exchange.getRequestMethod())) {
                if (userServiceImpl.getRoleByLogin(exchange.getPrincipal().getUsername()).equals(Role.EMPLOYEE)) {
                    Map<String, String> requestQuery = QueryParser.queryToMap(exchange.getRequestURI().getRawQuery());
                    if (requestQuery.get("id") != null && requestQuery.get("action") != null) {
                        try {
                            if (operationServiceImpl.changeStatusOperation(
                                    Long.parseLong(requestQuery.get("id")),
                                    requestQuery.get("action"))) {
                                exchange.sendResponseHeaders(200, -1);
                            } else {
                                exchange.sendResponseHeaders(406, -1);
                            }
                        } catch (OperationNotFoundException e) {
                            System.out.println("Operation not found");
                            exchange.sendResponseHeaders(404, -1);
                        }
                    } else {
                        exchange.sendResponseHeaders(404, -1);
                    }
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        } catch (IOException e) {
            System.out.println("IO error");
        } catch (UserNotFoundException e) {
            System.out.println("Uawe nor found");
        }
    }
}
