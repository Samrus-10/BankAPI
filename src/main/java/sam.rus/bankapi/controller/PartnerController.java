package sam.rus.bankapi.controller;

import sam.rus.bankapi.entity.Partner;
import sam.rus.bankapi.util.enums.RequestMethod;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.util.exception.PartnerNotFoundException;
import sam.rus.bankapi.util.exception.UserNotFoundException;
import sam.rus.bankapi.util.convertor.PartnerConvertor;
import sam.rus.bankapi.service.Impl.PartnerServiceImpl;
import sam.rus.bankapi.service.Impl.UserServiceImpl;
import sam.rus.bankapi.util.QueryParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class PartnerController implements HttpHandler {
    private  PartnerServiceImpl partnerServiceImpl = new PartnerServiceImpl();
    private  UserServiceImpl userServiceImpl = new UserServiceImpl();
    private final PartnerConvertor partnerMapper = new PartnerConvertor();

    public PartnerController() {
    }

    public PartnerController(PartnerServiceImpl partnerServiceImpl, UserServiceImpl userServiceImpl) {
        this.partnerServiceImpl = partnerServiceImpl;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            if (RequestMethod.GET.toString().equals(exchange.getRequestMethod())) {
                if (userServiceImpl.getRoleByLogin(exchange.getPrincipal().getUsername()).equals(Role.USER)) {
                    Map<String, String> requestQuery = QueryParser.queryToMap(exchange.getRequestURI().getRawQuery());
                    if (requestQuery.isEmpty()) {
                        try {
                            List<Partner> partnerList = partnerServiceImpl.getAllPartners();
                            exchange.sendResponseHeaders(200,
                                    partnerMapper.PartnerListToJson(partnerList).getBytes().length);
                            OutputStream outputStream = exchange.getResponseBody();
                            outputStream.write(partnerMapper.PartnerListToJson(partnerList).getBytes());
                            outputStream.flush();
                            outputStream.close();
                        } catch (PartnerNotFoundException e) {
                            e.printStackTrace();
                            exchange.sendResponseHeaders(404, -1);
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
                        Partner partner = partnerMapper.JsonToPartner(exchange);
                        if (partnerServiceImpl.addPartner(partner)) {
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
