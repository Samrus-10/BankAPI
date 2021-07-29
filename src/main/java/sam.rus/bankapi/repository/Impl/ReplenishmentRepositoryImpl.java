package sam.rus.bankapi.repository.Impl;

import sam.rus.bankapi.entity.Replenishment;
import sam.rus.bankapi.repository.ConnectionPoll;
import sam.rus.bankapi.repository.ReplenishmentRepository;
import sam.rus.bankapi.util.PropertiesManager;
import sam.rus.bankapi.util.QuerySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReplenishmentRepositoryImpl implements ReplenishmentRepository {
    private final ConnectionPoll connectionPoll = ConnectionPollImpl.getInstance();

    @Override
    public boolean addReplenishment(Replenishment replenishment) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.ADD_REPLENISHMENT)) {
            preparedStatement.setDouble(1, replenishment.getSum());
            preparedStatement.setLong(2, replenishment.getBillId());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Replenishment> getAllReplenishmentByBill(long billId) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(QuerySQL.GET_ALL_REPLENISHMENT_BY_BILL)) {
            preparedStatement.setLong(1, billId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Replenishment> replenishmentList = new ArrayList<>();
            while (resultSet.next()) {
                Replenishment replenishment = new Replenishment(
                        resultSet.getLong(1),
                        resultSet.getDouble(2),
                        resultSet.getLong(3)
                );
                replenishmentList.add(replenishment);
            }
            return replenishmentList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
