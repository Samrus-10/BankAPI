package sam.rus.bankapi.repository.Impl;

import sam.rus.bankapi.entity.Card;
import sam.rus.bankapi.repository.CardRepository;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CardRepositoryImplTest {
    private final CardRepository cardRepository = new CardRepositoryImpl();
    private ResultSet resultSet;
    private static final String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String createTable = "src/test/resources/SQLScripts/Card/CreateTableCard.sql";
    private static final String deleteTable = "src/test/resources/SQLScripts/Card/DeleteTableCard.sql";

    @Test
    public void addCard() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        //cardRepository.setUrl(url);
        RunScript.execute(connection, new FileReader(createTable));
        assertTrue(cardRepository.addCard(new Card()));
        RunScript.execute(connection, new FileReader(deleteTable));
        assertFalse(cardRepository.addCard(new Card()));
        connection.close();
    }

    @Test
    public void getAllCardsByBill() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        RunScript.execute(connection, new FileReader(createTable));
        //cardRepository.setUrl(url);
        PreparedStatement card1 =
                connection.prepareStatement("INSERT INTO CARDS (EXPIRES, FIRST_NAME, LAST_NAME, BILL_ID) " +
                        "VALUES ( '01/01', 'V', 'K', 1 )");
        PreparedStatement card2 =
                connection.prepareStatement("INSERT INTO CARDS (EXPIRES, FIRST_NAME, LAST_NAME, BILL_ID) " +
                        "VALUES ( '02/02', 'Q', 'W', 1 )");
        card1.execute();
        card2.execute();
        PreparedStatement getAllCard = connection.prepareStatement("SELECT * FROM CARDS WHERE BILL_ID = 1");
        resultSet = getAllCard.executeQuery();
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
        assertEquals(cardList, cardRepository.getAllCardsByBill(1));
        card1.close();
        card2.close();
        getAllCard.close();
        RunScript.execute(connection, new FileReader(deleteTable));
        assertNull(cardRepository.getAllCardsByBill(1));
        connection.close();
    }

    @Test
    public void getCardById() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        RunScript.execute(connection, new FileReader(createTable));
        //cardRepository.setUrl(url);
        PreparedStatement addCard =
                connection.prepareStatement("INSERT INTO CARDS (EXPIRES, FIRST_NAME, LAST_NAME, BILL_ID) " +
                        "VALUES ( '01/01', 'V', 'K', 1 )");
        addCard.execute();
        PreparedStatement getCard = connection.prepareStatement("SELECT * FROM CARDS WHERE ID = 1");
        resultSet = getCard.executeQuery();
        resultSet.next();
        Card card = new Card(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getLong(6),
                resultSet.getString(7));

        assertEquals(Optional.of(card), cardRepository.getCardById(1));
        addCard.close();
        getCard.close();
        RunScript.execute(connection, new FileReader(deleteTable));
        assertEquals(Optional.empty(), cardRepository.getCardById(1));
        connection.close();
    }

    @Test
    public void getAllCards() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        RunScript.execute(connection, new FileReader(createTable));
        //cardRepository.setUrl(url);
        PreparedStatement card1 =
                connection.prepareStatement("INSERT INTO CARDS (EXPIRES, FIRST_NAME, LAST_NAME, BILL_ID) " +
                        "VALUES ( '01/01', 'V', 'K', 1 )");
        PreparedStatement card2 =
                connection.prepareStatement("INSERT INTO CARDS (EXPIRES, FIRST_NAME, LAST_NAME, BILL_ID) " +
                        "VALUES ( '02/02', 'Q', 'W', 1 )");
        card1.execute();
        card2.execute();
        PreparedStatement getAllCard = connection.prepareStatement("SELECT * FROM CARDS");
        resultSet = getAllCard.executeQuery();
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
        assertEquals(cardList, cardRepository.getAllCards());
        card1.close();
        card2.close();
        getAllCard.close();
        RunScript.execute(connection, new FileReader(deleteTable));
        assertNull(cardRepository.getAllCards());
        connection.close();
    }

    @Test
    public void getAllCardsByStatus() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        RunScript.execute(connection, new FileReader(createTable));
        //cardRepository.setUrl(url);
        PreparedStatement card1 =
                connection.prepareStatement("INSERT INTO CARDS (EXPIRES, FIRST_NAME, LAST_NAME, BILL_ID) " +
                        "VALUES ( '01/01', 'V', 'K', 1 )");
        PreparedStatement card2 =
                connection.prepareStatement("INSERT INTO CARDS (EXPIRES, FIRST_NAME, LAST_NAME, BILL_ID) " +
                        "VALUES ( '02/02', 'Q', 'W', 2 )");
        card1.execute();
        card2.execute();
        PreparedStatement getAllCard =
                connection.prepareStatement("SELECT * FROM CARDS WHERE STATUS = 'NOT_ACTIVE'");
        resultSet = getAllCard.executeQuery();
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
        assertEquals(cardList, cardRepository.getAllCardsByStatus("NOT_ACTIVE"));
        card1.close();
        card2.close();
        getAllCard.close();
        RunScript.execute(connection, new FileReader(deleteTable));
        assertNull(cardRepository.getAllCardsByStatus("NOT_ACTIVE"));
        connection.close();
    }

    @Test
    public void changeCardStatus() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        RunScript.execute(connection, new FileReader(createTable));
        //cardRepository.setUrl(url);
        PreparedStatement addCard =
                connection.prepareStatement("INSERT INTO CARDS (EXPIRES, FIRST_NAME, LAST_NAME, BILL_ID) " +
                        "VALUES ( '01/01', 'V', 'K', 1 )");
        addCard.execute();
        assertTrue(cardRepository.changeCardStatus(1, "active"));
        addCard.close();
        RunScript.execute(connection, new FileReader(deleteTable));
        assertFalse(cardRepository.changeCardStatus(1, "active"));
        connection.close();
    }
}