package sam.rus.bankapi.repository.Impl;

import sam.rus.bankapi.entity.Bill;
import sam.rus.bankapi.repository.BillRepository;
import sam.rus.bankapi.repository.ConnectionPoll;
import sam.rus.bankapi.util.QuerySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BillRepositoryImpl implements BillRepository {
    private final ConnectionPoll connectionPoll = ConnectionPollImpl.getInstance();
    private ResultSet resultSet;

    @Override
    public boolean addBill(long userId) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.ADD_BILL)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<Bill> getBillById(long billId) {
        try (
                Connection connection = connectionPoll.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.GET_BILL_BY_ID))
        {
            preparedStatement.setLong(1, billId);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Bill bill = new Bill(
                    resultSet.getLong(1),
                    resultSet.getLong(2),
                    resultSet.getDouble(3),
                    resultSet.getLong(4)
            );
            return Optional.of(bill);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Bill> getAllBillsByUser(long userId) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.GET_ALL_BILLS_BY_USER)) {
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();
            List<Bill> billList = new ArrayList<>();
            while (resultSet.next()) {
                Bill bill = new Bill(
                        resultSet.getLong(1),
                        resultSet.getLong(2),
                        resultSet.getDouble(3),
                        resultSet.getLong(4)
                );
                billList.add(bill);
            }
            return billList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public double getBalanceBill(long billId) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.GET_BALANCE_BILL_BY_ID)) {
            preparedStatement.setLong(1, billId);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean plusBalance(long billId, double sum) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.PLUS_BALANCE_BY_BILL)) {
            preparedStatement.setDouble(1, sum);
            preparedStatement.setLong(2, billId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean minusBalance(long billId, double sum) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.MINUS_BALANCE_BY_BILL)) {
            preparedStatement.setDouble(1, sum);
            preparedStatement.setLong(2, billId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
