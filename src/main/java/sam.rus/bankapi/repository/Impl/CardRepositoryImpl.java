package sam.rus.bankapi.repository.Impl;

import sam.rus.bankapi.entity.Card;
import sam.rus.bankapi.repository.CardRepository;
import sam.rus.bankapi.repository.ConnectionPoll;
import sam.rus.bankapi.util.PropertiesManager;
import sam.rus.bankapi.util.QuerySQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardRepositoryImpl implements CardRepository {
    private final ConnectionPoll connectionPoll = ConnectionPollImpl.getInstance();
    private ResultSet resultSet;



    @Override
    public boolean addCard(Card card) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.ADD_CARD)) {
            preparedStatement.setString(1, card.getExpires());
            preparedStatement.setString(2, card.getFirstName());
            preparedStatement.setString(3, card.getLastName());
            preparedStatement.setLong(4, card.getBillId());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Card> getAllCardsByBill(long billId) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.GET_ALL_CARDS_BY_BILL)) {
            preparedStatement.setLong(1, billId);
            resultSet = preparedStatement.executeQuery();
            List<Card> cardList = new ArrayList<>();
            while (resultSet.next()) {
                Card card = new Card(
                        resultSet.getLong(1),
                        resultSet.getLong(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getLong(6),
                        resultSet.getString(7)
                );
                cardList.add(card);
            }
            return cardList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Optional<Card> getCardById(long cardId) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.GET_CARD_BY_ID)) {
            preparedStatement.setLong(1, cardId);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Card card = new Card(
                    resultSet.getLong(1),
                    resultSet.getLong(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getLong(6),
                    resultSet.getString(7)
            );
            return Optional.of(card);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Card> getAllCards() {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.GET_ALL_CARDS)) {
            resultSet = preparedStatement.executeQuery();
            List<Card> cardList = new ArrayList<>();
            while (resultSet.next()) {
                Card card = new Card(
                        resultSet.getLong(1),
                        resultSet.getLong(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getLong(6),
                        resultSet.getString(7)
                );
                cardList.add(card);
            }
            return cardList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Card> getAllCardsByStatus(String status) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.GET_ALL_CARDS_BY_STATUS)) {
            preparedStatement.setString(1, status);
            resultSet = preparedStatement.executeQuery();
            List<Card> cardList = new ArrayList<>();
            while (resultSet.next()) {
                Card card = new Card(
                        resultSet.getLong(1),
                        resultSet.getLong(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getLong(6),
                        resultSet.getString(7)
                );
                cardList.add(card);
            }
            return cardList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean changeCardStatus(long cardId, String status) {
        try (Connection connection = connectionPoll.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QuerySQL.CHANGE_CARD_STATUS)) {
            preparedStatement.setString(1, status);
            preparedStatement.setLong(2, cardId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
