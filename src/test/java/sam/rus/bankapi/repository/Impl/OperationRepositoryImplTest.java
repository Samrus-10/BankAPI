package sam.rus.bankapi.repository.Impl;

import sam.rus.bankapi.entity.Operation;
import sam.rus.bankapi.repository.OperationRepository;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OperationRepositoryImplTest {
    private final OperationRepository operationRepository = new OperationRepositoryImpl();
    private ResultSet resultSet;
    private static final String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String createTable = "src/test/resources/SQLScripts/Operation/CreateTableOperation.sql";
    private static final String deleteTable = "src/test/resources/SQLScripts/Operation/DeleteTableOperation.sql";

    @Test
    public void addOperation() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        //operationRepository.setUrl(url);
        RunScript.execute(connection, new FileReader(createTable));
        assertTrue(operationRepository.addOperation(new Operation()));
        RunScript.execute(connection, new FileReader(deleteTable));
        assertFalse(operationRepository.addOperation(new Operation()));
        connection.close();
    }

    @Test
    public void getAllOperationByBill() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        RunScript.execute(connection, new FileReader(createTable));
        //operationRepository.setUrl(url);
        PreparedStatement operation1 =
                connection.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 1, 1, 100 )");
        PreparedStatement operation2 =
                connection.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 1, 2, 200 )");
        operation1.execute();
        operation2.execute();
        PreparedStatement getAllOperation =
                connection.prepareStatement("SELECT * FROM OPERATIONS WHERE SOURCE = 1");
        resultSet = getAllOperation.executeQuery();
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
        assertEquals(operationList, operationRepository.getAllOperationByBill(1));
        operation1.close();
        operation2.close();
        getAllOperation.close();
        RunScript.execute(connection, new FileReader(deleteTable));
        assertNull(operationRepository.getAllOperationByBill(1));
        connection.close();
    }

    @Test
    public void getAllOperation() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        RunScript.execute(connection, new FileReader(createTable));
        //operationRepository.setUrl(url);
        PreparedStatement operation1 =
                connection.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 1, 1, 100 )");
        PreparedStatement operation2 =
                connection.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 2, 2, 200 )");
        operation1.execute();
        operation2.execute();
        PreparedStatement getAllOperation = connection.prepareStatement("SELECT * FROM OPERATIONS");
        resultSet = getAllOperation.executeQuery();
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
        assertEquals(operationList, operationRepository.getAllOperation());
        operation1.close();
        operation2.close();
        getAllOperation.close();
        RunScript.execute(connection, new FileReader(deleteTable));
        assertNull(operationRepository.getAllOperation());
        connection.close();
    }

    @Test
    public void getAllOperationsByStatus() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        RunScript.execute(connection, new FileReader(createTable));
        //operationRepository.setUrl(url);
        PreparedStatement operation1 =
                connection.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 1, 1, 100 )");
        PreparedStatement operation2 =
                connection.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 2, 2, 200 )");
        operation1.execute();
        operation2.execute();
        PreparedStatement getAllOperation =
                connection.prepareStatement("SELECT * FROM OPERATIONS WHERE STATUS = 'UNAPPROVED'");
        resultSet = getAllOperation.executeQuery();
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
        assertEquals(operationList, operationRepository.getAllOperationsByStatus("UNAPPROVED"));
        operation1.close();
        operation2.close();
        getAllOperation.close();
        RunScript.execute(connection, new FileReader(deleteTable));
        assertNull(operationRepository.getAllOperationsByStatus("UNAPPROVED"));
        connection.close();
    }

    @Test
    public void changeOperationStatus() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        RunScript.execute(connection, new FileReader(createTable));
        //operationRepository.setUrl(url);
        PreparedStatement operation1 =
                connection.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 1, 1, 100 )");
        operation1.execute();
        assertTrue(operationRepository.changeOperationStatus(1, "APPROVED"));
        operation1.close();
        RunScript.execute(connection, new FileReader(deleteTable));
        assertFalse(operationRepository.changeOperationStatus(1, "APPROVED"));
        connection.close();
    }

    @Test
    public void getOperationById() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        RunScript.execute(connection, new FileReader(createTable));
        //operationRepository.setUrl(url);
        PreparedStatement operation1 =
                connection.prepareStatement(
                        "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( 1, 1, 100 )");
        operation1.execute();
        PreparedStatement getOperation = connection.prepareStatement("SELECT * FROM OPERATIONS WHERE ID = 1");
        resultSet = getOperation.executeQuery();
        resultSet.next();
        Operation operation = new Operation(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getDouble(4),
                resultSet.getString(5));

        assertEquals(Optional.of(operation), operationRepository.getOperationById(1));
        operation1.close();
        getOperation.close();
        RunScript.execute(connection, new FileReader(deleteTable));
        assertEquals(Optional.empty(), operationRepository.getOperationById(1));
        connection.close();
    }
}