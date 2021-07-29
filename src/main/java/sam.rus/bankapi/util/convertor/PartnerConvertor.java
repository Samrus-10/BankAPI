package sam.rus.bankapi.util.convertor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sam.rus.bankapi.entity.Partner;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.List;

public class PartnerConvertor {
    private final ObjectMapper mapper = new ObjectMapper();

    public Partner JsonToPartner(HttpExchange exchange) {
        try {
            return mapper.readValue(exchange.getRequestBody(), Partner.class);
        } catch (IOException e) {
            System.out.println("Json processing error");
            return null;
        }
    }

    public String PartnerListToJson(List<Partner> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            System.out.println("Json processing error");
            return null;
        }
    }
}
