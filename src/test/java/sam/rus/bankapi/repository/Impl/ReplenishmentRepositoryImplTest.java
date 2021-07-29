package sam.rus.bankapi.dao.repository.Impl;

import sam.rus.bankapi.entity.Replenishment;
import sam.rus.bankapi.repository.ReplenishmentRepository;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReplenishmentRepositoryImplTest {
    private final ReplenishmentRepository replenishmentRepository = new sam.rus.bankapi.repository.Impl.ReplenishmentRepositoryImpl();
    private static final String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private ResultSet resultSet;
    private static final String createTable = "src/test/resources/SQLScripts/Replenishment/CreateTableReplenishment.sql";
    private static final String deleteTable = "src/test/resources/SQLScripts/Replenishment/DeleteTableReplenishment.sql";

    @Test
    public void addReplenishment() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        //replenishmentRepository.setUrl(url);
        RunScript.execute(connection, new FileReader(createTable));
        assertTrue(replenishmentRepository.addReplenishment(new Replenishment()));
        RunScript.execute(connection, new FileReader(deleteTable));
        assertFalse(replenishmentRepository.addReplenishment(new Replenishment()));
        connection.close();
    }

    @Test
    public void getAllReplenishmentByBill() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        RunScript.execute(connection, new FileReader(createTable));
        //replenishmentRepository.setUrl(url);
        PreparedStatement replenishment1 =
                connection.prepareStatement("INSERT INTO REPLENISHMENTS (SUM, BILL_ID) VALUES ( 100, 1 )");
        PreparedStatement replenishment2 =
                connection.prepareStatement("INSERT INTO REPLENISHMENTS (SUM, BILL_ID) VALUES ( 200, 1 )");
        replenishment1.execute();
        replenishment2.execute();
        PreparedStatement getAllReplenishment =
                connection.prepareStatement("SELECT * FROM REPLENISHMENTS WHERE BILL_ID = 1");
        ResultSet resultSet = getAllReplenishment.executeQuery();
        List<Replenishment> replenishmentList = new ArrayList<>();
        while (resultSet.next()) {
            Replenishment replenishment = new Replenishment(
                    resultSet.getLong(1),
                    resultSet.getDouble(2),
                    resultSet.getLong(3)
            );
            replenishmentList.add(replenishment);
        }
        assertEquals(replenishmentList, replenishmentRepository.getAllReplenishmentByBill(1));
        replenishment1.close();
        replenishment2.close();
        getAllReplenishment.close();
        RunScript.execute(connection, new FileReader(deleteTable));
        assertNull(replenishmentRepository.getAllReplenishmentByBill(1));
        connection.close();
    }
}