package sam.rus.bankapi.util.convertor;

import com.fasterxml.jackson.databind.ObjectMapper;
import sam.rus.bankapi.entity.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;


public class UserConvertor {
    private final ObjectMapper mapper = new ObjectMapper();

    public User JsonToUser(HttpExchange exchange) {
        try {
            return mapper.readValue(exchange.getRequestBody(), User.class);
        } catch (IOException e) {
            System.out.println("Json processing error");
            return null;
        }
    }
}
