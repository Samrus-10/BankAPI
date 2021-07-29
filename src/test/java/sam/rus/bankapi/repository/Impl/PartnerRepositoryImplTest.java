package sam.rus.bankapi.repository.Impl;

import sam.rus.bankapi.entity.Partner;
import sam.rus.bankapi.repository.PartnerRepository;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PartnerRepositoryImplTest {
    private final PartnerRepository partnerRepository = new PartnerRepositoryImpl();
    private static final String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String createTable = "src/test/resources/SQLScripts/Partner/CreateTablePartner.sql";
    private static final String deleteTable = "src/test/resources/SQLScripts/Partner/DeleteTalbePartner.sql";


    @Test
    public void addPartner() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        //partnerRepository.setUrl(url);
        RunScript.execute(connection, new FileReader(createTable));
        Partner partner = new Partner();
        partner.setPartnerBill(2000000000000000001L);
        assertTrue(partnerRepository.addPartner(partner));
        RunScript.execute(connection, new FileReader(deleteTable));
        assertFalse(partnerRepository.addPartner(new Partner()));
        connection.close();
    }

    @Test
    public void getAllPartners() throws SQLException, FileNotFoundException {
        Connection connection = DriverManager.getConnection(url);
        RunScript.execute(connection, new FileReader(createTable));
        //partnerRepository.setUrl(url);
        PreparedStatement partner1 =
                connection.prepareStatement(
                        "INSERT INTO PARTNERS (NAME, PARTNER_BILL) VALUES ( 'V', 2000000000000000001 )");
        PreparedStatement partner2 =
                connection.prepareStatement(
                        "INSERT INTO PARTNERS (NAME, PARTNER_BILL) VALUES ( 'K', 2000000000000000002 )");
        partner1.execute();
        partner2.execute();
        PreparedStatement getAllPartners = connection.prepareStatement("SELECT * FROM PARTNERS");
        ResultSet resultSet = getAllPartners.executeQuery();
        List<Partner> partnerList = new ArrayList<>();
        while (resultSet.next()) {
            Partner partner = new Partner(
                    resultSet.getLong(1),
                    resultSet.getString(2),
                    resultSet.getLong(3)
            );
            partnerList.add(partner);
        }
        assertEquals(partnerList, partnerRepository.getAllPartners());
        partner1.close();
        partner2.close();
        getAllPartners.close();
        RunScript.execute(connection, new FileReader(deleteTable));
        assertNull(partnerRepository.getAllPartners());
        connection.close();
    }
}