package sam.rus.bankapi.util;

public class QuerySQL {
    public static final String ADD_USER =
            "INSERT INTO USERS (LOGIN, PASSWORD, FIRST_NAME, LAST_NAME, MIDDLE_NAME, PASSPORT, MOBILE_PHONE, ROLE) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String GET_USER_BY_LOGIN = "SELECT * FROM USERS WHERE LOGIN = ?";

    public static final String ADD_BILL = "INSERT INTO BILLS(USER_ID) VALUES (?)";

    public static final String GET_BILL_BY_ID = "SELECT * FROM BILLS WHERE ID = ?";

    public static final String GET_ALL_BILLS_BY_USER = "SELECT * FROM BILLS WHERE USER_ID = ?";

    public static final String GET_BALANCE_BILL_BY_ID = "SELECT BALANCE FROM BILLS WHERE ID = ?";

    public static final String PLUS_BALANCE_BY_BILL =
            "UPDATE BILLS SET BALANCE = (SELECT BALANCE + ?) WHERE ID = ?";

    public static final String MINUS_BALANCE_BY_BILL =
            "UPDATE BILLS SET BALANCE = (SELECT BALANCE - ?) WHERE ID = ?";

    public static final String ADD_CARD =
            "INSERT INTO CARDS (EXPIRES, FIRST_NAME, LAST_NAME, BILL_ID) VALUES ( ?, ?, ?, ? )";

    public static final String GET_ALL_CARDS_BY_BILL = "SELECT * FROM CARDS WHERE BILL_ID = ?";

    public static final String GET_CARD_BY_ID = "SELECT * FROM CARDS WHERE ID = ?";

    public static final String GET_ALL_CARDS = "SELECT * FROM CARDS";

    public static final String GET_ALL_CARDS_BY_STATUS = "SELECT * FROM CARDS WHERE STATUS = ?";

    public static final String CHANGE_CARD_STATUS = "UPDATE CARDS SET STATUS = ? WHERE ID = ?";

    public static final String ADD_OPERATION = "INSERT INTO OPERATIONS (SOURCE, TARGET, SUM)  VALUES ( ?, ?, ? )";

    public static final String GET_ALL_OPERATION_BY_BILL = "SELECT * FROM OPERATIONS WHERE SOURCE = ?";

    public static final String GET_ALL_OPERATION = "SELECT * FROM OPERATIONS";

    public static final String GET_ALL_OPERATIONS_BY_STATUS = "SELECT * FROM OPERATIONS WHERE STATUS = ?";

    public static final String CHANGE_OPERATION_STATUS = "UPDATE OPERATIONS SET STATUS = ? WHERE  ID = ?";

    public static final String GET_OPERATION_BY_ID = "SELECT * FROM  OPERATIONS WHERE ID = ?";

    public static final String ADD_PARTNER = "INSERT INTO PARTNERS (NAME, PARTNER_BILL) VALUES ( ?, ? )";

    public static final String GET_ALL_PARTNERS = "SELECT * FROM PARTNERS";

    public static final String ADD_REPLENISHMENT = "INSERT INTO REPLENISHMENTS (SUM, BILL_ID) VALUES ( ?, ? )";

    public static final String GET_ALL_REPLENISHMENT_BY_BILL = "SELECT * FROM REPLENISHMENTS WHERE BILL_ID = ?";

}

