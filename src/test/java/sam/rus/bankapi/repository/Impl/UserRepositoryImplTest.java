package sam.rus.bankapi.repository.Impl;

import org.h2.tools.RunScript;
import org.junit.jupiter.api.Test;
import sam.rus.bankapi.entity.User;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.repository.UserRepository;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryImplTest {
    private final UserRepository userRepository = new UserRepositoryImpl();
    private static final String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String createTable = "src/test/resources/SQLScripts/User/CreateTableUser";
    private static final String deleteTable = "src/test/resources/SQLScripts/User/DeleteTableUser";

    @Test
    public void addUser() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        //userRepository.setUrl(url);
        RunScript.execute(connection, new FileReader(createTable));
        User user = new User(
                "login",
                "password",
                "first",
                "second",
                "middle",
                "123",
                "89",
                Role.USER);
        assertTrue(userRepository.addUser(user));
        RunScript.execute(connection, new FileReader(deleteTable));
        assertFalse(userRepository.addUser(new User()));
        connection.close();
    }

    @Test
    public void getUserByLogin() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        RunScript.execute(connection, new FileReader(createTable));
        //userRepository.setUrl(url);
        PreparedStatement addUser =
                connection.prepareStatement("INSERT INTO USERS " +
                        "(LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PASSPORT, MOBILE_PHONE, ROLE) " +
                        "VALUES ('l', 'p', 'f', 'l', 'm', 'p', 'm', 'USER')");
        addUser.execute();
        PreparedStatement getUser = connection.prepareStatement("SELECT * FROM USERS WHERE LOGIN = 'l'");
        ResultSet resultSet = getUser.executeQuery();
        resultSet.next();
        User user = new User(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6),
                resultSet.getString(7),
                resultSet.getString(8),
                Role.valueOf(resultSet.getString(9)));
        assertEquals(Optional.of(user), userRepository.getUserByLogin("l"));
        addUser.close();
        getUser.close();
        RunScript.execute(connection, new FileReader(deleteTable));
        assertEquals(Optional.empty(), userRepository.getUserByLogin("l"));
        connection.close();
    }
}