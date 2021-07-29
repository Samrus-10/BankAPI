package sam.rus.bankapi.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncryption {
    public static String hashedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String candidate, String password) {
        return BCrypt.checkpw(candidate, password);
    }
}
