package sam.rus.bankapi.util.exception;

public class CardNotFoundException extends Exception {
    public CardNotFoundException() {
        super("Card not found exception");
    }
}
