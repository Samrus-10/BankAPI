package sam.rus.bankapi.repository.Impl;

import sam.rus.bankapi.entity.User;
import sam.rus.bankapi.util.enums.Role;
import sam.rus.bankapi.repository.ConnectionPoll;
import sam.rus.bankapi.repository.UserRepository;
import sam.rus.bankapi.util.PropertiesManager;
import sam.rus.bankapi.util.QuerySQL;

import java.sql.*;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private final ConnectionPoll connectionPoll = ConnectionPollImpl.getInstance();


    @Override
    public boolean addUser(User user) {
        boolean result = false;
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.ADD_USER)) {
            preparedStatement.setString(1, user.getLogin());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getLastName());
            preparedStatement.setString(5, user.getMiddleName());
            preparedStatement.setString(6, user.getPassport());
            preparedStatement.setString(7, user.getMobilePhone());
            preparedStatement.setString(8, user.getRole().toString());
            result = preparedStatement.execute();
            return result;
        } catch (SQLException e) {
            System.out.println("SQL error");
            return result;
        }
    }

    @Override
    public Optional<User> getUserByLogin(String login) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.GET_USER_BY_LOGIN)) {
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
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
            return Optional.of(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
