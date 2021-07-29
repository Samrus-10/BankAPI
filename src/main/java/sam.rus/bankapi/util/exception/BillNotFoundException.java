package sam.rus.bankapi.util.exception;

public class BillNotFoundException extends Exception {
    public BillNotFoundException() {
        super("Bill not found exception");
    }
}
