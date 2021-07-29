package sam.rus.bankapi.repository.Impl;

import sam.rus.bankapi.entity.Partner;
import sam.rus.bankapi.repository.ConnectionPoll;
import sam.rus.bankapi.repository.PartnerRepository;
import sam.rus.bankapi.util.PropertiesManager;
import sam.rus.bankapi.util.QuerySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartnerRepositoryImpl implements PartnerRepository {
    private final ConnectionPoll connectionPoll = ConnectionPollImpl.getInstance();

    @Override
    public boolean addPartner(Partner partner) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.ADD_PARTNER)) {
            preparedStatement.setString(1, partner.getName());
            preparedStatement.setLong(2, partner.getPartnerBill());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Partner> getAllPartners() {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.GET_ALL_PARTNERS)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Partner> partnerList = new ArrayList<>();
            while (resultSet.next()) {
                Partner partner = new Partner(
                        resultSet.getLong(1),
                        resultSet.getString(2),
                        resultSet.getLong(3)
                );
                partnerList.add(partner);
            }
            return partnerList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
