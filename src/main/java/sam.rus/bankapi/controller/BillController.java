package sam.rus.bankapi.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import sam.rus.bankapi.entity.Bill;
import sam.rus.bankapi.service.Impl.BillServiceImpl;
import sam.rus.bankapi.service.Impl.UserServiceImpl;
import sam.rus.bankapi.util.QueryParser;
import sam.rus.bankapi.util.convertor.BillConvertor;
import sam.rus.bankapi.util.enums.RequestMethod;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.BillNotFoundException;
import sam.rus.bankapi.util.exception.NoAccessException;
import sam.rus.bankapi.util.exception.UserNotFoundException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class BillController implements HttpHandler {
    private BillServiceImpl billServiceImpl = new BillServiceImpl();
    private UserServiceImpl userServiceImpl = new UserServiceImpl();
    private final BillConvertor billMapper = new BillConvertor();

    public BillController() {

    }

    public BillController(BillServiceImpl billServiceImpl, UserServiceImpl userServiceImpl) {
        this.billServiceImpl = billServiceImpl;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (RequestMethod.GET.toString().equalsIgnoreCase(exchange.getRequestMethod())) {
                getHandler(exchange);
            } else if (RequestMethod.POST.toString().equalsIgnoreCase(exchange.getRequestMethod())) {
                postHandler(exchange);
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getHandler(HttpExchange exchange) throws UserNotFoundException, IOException {
        if (userServiceImpl.getRoleByLogin(exchange.getPrincipal().getUsername()).equals(Role.USER)) {
            Map<String, String> requestQuery = QueryParser.queryToMap(exchange.getRequestURI().getRawQuery());
            if (requestQuery.get("id") != null) {
                Bill bill = null;
                try {
                    bill = billServiceImpl.getBillByIdAndLogin(Long.parseLong(requestQuery.get("id")),
                            exchange.getPrincipal().getUsername());
                } catch (NoAccessException e) {
                    e.printStackTrace();
                } catch (BillNotFoundException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(404, -1);
                }
                exchange.sendResponseHeaders(200, billMapper.BillToJson(bill).getBytes().length);
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(billMapper.BillToJson(bill).getBytes());
                outputStream.flush();
                outputStream.close();
            } else if (requestQuery.get("billId") != null) {
                try {
                    double balance = billServiceImpl.getBalance(Long.parseLong(requestQuery.get("billId")),
                            exchange.getPrincipal().getUsername());
                    exchange.sendResponseHeaders(200,
                            billMapper.balanceToJson(balance).getBytes().length);
                    OutputStream outputStream = exchange.getResponseBody();
                    outputStream.write(billMapper.balanceToJson(balance).getBytes());
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoAccessException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(403, -1);
                } catch (BillNotFoundException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(404, -1);
                }
            } else if (requestQuery.isEmpty()) {
                List<Bill> billList =
                        billServiceImpl.getAllBillsByUser
                                (userServiceImpl.getUserIdByLogin(exchange.getPrincipal().getUsername()));
                exchange.sendResponseHeaders(200, billMapper.BillListToJson(billList).getBytes().length);
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(billMapper.BillListToJson(billList).getBytes());
                outputStream.flush();
                outputStream.close();
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        } else {
            exchange.sendResponseHeaders(403, -1);
        }
    }

    public void postHandler(HttpExchange exchange) throws UserNotFoundException, IOException {
        if (userServiceImpl.getRoleByLogin(exchange.getPrincipal().getUsername()).equals(Role.EMPLOYEE)) {
            Map<String, String> requestQuery = QueryParser.queryToMap(exchange.getRequestURI().getRawQuery());
            if (requestQuery.get("id") != null) {
                if (billServiceImpl.addBill(Long.parseLong(requestQuery.get("id")))) {
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
    }
}




