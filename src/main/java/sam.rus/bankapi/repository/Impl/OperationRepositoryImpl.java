package sam.rus.bankapi.repository.Impl;

import sam.rus.bankapi.entity.Operation;
import sam.rus.bankapi.repository.ConnectionPoll;
import sam.rus.bankapi.repository.OperationRepository;
import sam.rus.bankapi.util.PropertiesManager;
import sam.rus.bankapi.util.QuerySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OperationRepositoryImpl implements OperationRepository {
    private final ConnectionPoll connectionPoll = ConnectionPollImpl.getInstance();
    private ResultSet resultSet;

    @Override
    public boolean addOperation(Operation operation) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.ADD_OPERATION)) {
            preparedStatement.setLong(1, operation.getSourceId());
            preparedStatement.setLong(2, operation.getTargetId());
            preparedStatement.setDouble(3, operation.getSum());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Operation> getAllOperationByBill(long billId) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.GET_ALL_OPERATION_BY_BILL)) {
            preparedStatement.setLong(1, billId);
            resultSet = preparedStatement.executeQuery();
            List<Operation> operationList = new ArrayList<>();
            while (resultSet.next()) {
                Operation operation = new Operation(
                        resultSet.getLong(1),
                        resultSet.getLong(2),
                        resultSet.getLong(3),
                        resultSet.getDouble(4),
                        resultSet.getString(5)
                );
                operationList.add(operation);
            }
            return operationList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Operation> getAllOperation() {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.GET_ALL_OPERATION)) {
            resultSet = preparedStatement.executeQuery();
            List<Operation> operationList = new ArrayList<>();
            while (resultSet.next()) {
                Operation operation = new Operation(
                        resultSet.getLong(1),
                        resultSet.getLong(2),
                        resultSet.getLong(3),
                        resultSet.getDouble(4),
                        resultSet.getString(5)
                );
                operationList.add(operation);
            }
            return operationList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Operation> getAllOperationsByStatus(String status) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.GET_ALL_OPERATIONS_BY_STATUS)) {
            preparedStatement.setString(1, status);
            resultSet = preparedStatement.executeQuery();
            List<Operation> operationList = new ArrayList<>();
            while (resultSet.next()) {
                Operation operation = new Operation(
                        resultSet.getLong(1),
                        resultSet.getLong(2),
                        resultSet.getLong(3),
                        resultSet.getDouble(4),
                        resultSet.getString(5)
                );
                operationList.add(operation);
            }
            return operationList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean changeOperationStatus(long operationId, String status) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.CHANGE_OPERATION_STATUS)) {
            preparedStatement.setString(1, status);
            preparedStatement.setLong(2, operationId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Operation> getOperationById(long operationId) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.GET_OPERATION_BY_ID)) {
            preparedStatement.setLong(1, operationId);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Operation operation = new Operation(
                    resultSet.getLong(1),
                    resultSet.getLong(2),
                    resultSet.getLong(3),
                    resultSet.getDouble(4),
                    resultSet.getString(5)
            );
            return Optional.of(operation);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
