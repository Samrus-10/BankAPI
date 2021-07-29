package sam.rus.bankapi.util.exception;

public class PartnerNotFoundException extends Exception {
    public PartnerNotFoundException() {
        super("Partner not found exception");
    }
}
