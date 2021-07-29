package sam.rus.bankapi.repository.Impl;

import org.h2.jdbcx.JdbcConnectionPool;
import sam.rus.bankapi.repository.ConnectionPoll;
import sam.rus.bankapi.util.PropertiesManager;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPollImpl implements ConnectionPoll {
    private String JDBC_DB_URL;
    private String JDBC_USER;
    private String JDBC_PASS;
    private JdbcConnectionPool connectionPool;
    private PropertiesManager propertiesManager = new PropertiesManager();
    private static ConnectionPoll instance;

    private ConnectionPollImpl() {

    }

    public static synchronized ConnectionPoll getInstance(){
        if(instance == null){
            synchronized (ConnectionPollImpl.class){
                if(instance == null){
                    instance = new ConnectionPollImpl();
                }
            }
        }
        return  instance;
    }


    @Override
    public synchronized Connection getConnection() {
        if (connectionPool == null) {
            init();
        }
        Connection result = null;
        try {
            result = connectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public synchronized boolean closeConnectionPool() {
        boolean result = false;
        if (connectionPool != null) {
            connectionPool.dispose();
            result = true;
        }
        return result;
    }

    private void init() {
        JDBC_DB_URL = propertiesManager.getUrl();
        JDBC_USER = propertiesManager.getName();
        JDBC_PASS = propertiesManager.getPassword();
        connectionPool = JdbcConnectionPool.create(JDBC_DB_URL, JDBC_USER, JDBC_PASS);
    }
}
