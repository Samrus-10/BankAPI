package sam.rus.bankapi.util.convertor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sam.rus.bankapi.entity.Operation;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class OperationConvertor {
    private final ObjectMapper mapper = new ObjectMapper();

    public Operation JsonToOperation(HttpExchange exchange) {
        try {
            return mapper.readValue(exchange.getRequestBody(), Operation.class);
        } catch (IOException e) {
            System.out.println("Json processing error");
            return null;
        }
    }

    public String OperationListToJson(List<Operation> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            System.out.println("Json processing error");
            return null;
        }
    }
}
