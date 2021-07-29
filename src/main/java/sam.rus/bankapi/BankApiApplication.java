package sam.rus.bankapi;

import sam.rus.bankapi.util.exception.StartServerException;
import sam.rus.bankapi.util.Server;

import java.io.FileNotFoundException;
import java.sql.SQLException;


public class BankApiApplication {
    public static void main(String[] args) throws StartServerException {
        try {
            Server.start();
        } catch (FileNotFoundException | SQLException e) {
            System.out.println("Server error");
        }
    }
}
