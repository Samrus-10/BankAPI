package sam.rus.bankapi.util.exception;

public class StartServerException extends Exception {
    public StartServerException() {
        super("Server is not starting ...");
    }
}
