package sam.rus.bankapi.util.convertor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sam.rus.bankapi.entity.Card;

import java.util.List;

public class CardConvertor {
    private final ObjectMapper mapper = new ObjectMapper();

    public String CardToJson(Card card) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(card);
        } catch (JsonProcessingException e) {
            System.out.println("Json processing error");
            return null;
        }
    }

    public String CardListToJson(List<Card> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            System.out.println("Json processing error");
            return null;
        }
    }
}
