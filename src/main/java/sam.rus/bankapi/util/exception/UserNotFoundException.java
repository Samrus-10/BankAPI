package sam.rus.bankapi.util.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        super("User not found exception");
    }
}
