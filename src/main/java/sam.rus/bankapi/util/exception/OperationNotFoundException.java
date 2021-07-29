package sam.rus.bankapi.util.exception;

public class OperationNotFoundException extends Exception {
    public OperationNotFoundException() {
        super("Operation not found");
    }
}
