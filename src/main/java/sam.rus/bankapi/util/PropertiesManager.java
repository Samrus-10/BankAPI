package sam.rus.bankapi.util;

import sam.rus.bankapi.BankApiApplication;

import java.io.IOException;
import java.util.Properties;

public class PropertiesManager {
    private static final String property = "/property/infoBD.property";
    private final Properties properties = new Properties();

    public String getUrl() {
        try {
            properties.load(BankApiApplication.class.getResourceAsStream(property));
            return properties.getProperty("connection.url");
        } catch (IOException e) {
            System.out.println("IO error");
        }
        return null;
    }

    public String getName(){
        String result = null;
        try {
            properties.load(BankApiApplication.class.getResourceAsStream(property));
            result =  properties.getProperty("NAME_USER");
        } catch (IOException e) {
            System.out.println("IO error");
        }
        return result;
    }

    public String getPassword(){
        String result = null;
        try {
            properties.load(BankApiApplication.class.getResourceAsStream(property));
            result =  properties.getProperty("PASSWORD_USER");
        } catch (IOException e) {
            System.out.println("IO error");
        }
        return result;
    }

    public int getPort() {
        try {
            properties.load(BankApiApplication.class.getResourceAsStream(property));
            return Integer.parseInt(properties.getProperty("server.port"));
        } catch (IOException e) {
            System.out.println("IO error");
        }
        return -1;
    }
}
