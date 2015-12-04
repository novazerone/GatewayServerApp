package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by user on 11/30/2015.
 */
public class ConnectionFactory {
//    private static final String URL = ;
//    private static final String USER = ;
//    private static final String PASSWORD = ;
//    private static final String DRIVER_CLASS = ;
    private static ConnectionFactory instance = new ConnectionFactory();

    //private constructor
    private ConnectionFactory() {
    	DbConfig.initialize();
        try {
            Class.forName(DbConfig.getDriverClass());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection createConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DbConfig.getUrl(), DbConfig.getUser(), DbConfig.getPassword());

        } catch (SQLException e) {
            System.out.println("ERROR: Unable to Connect to Database.");
        }
        return connection;
    }

    public static Connection getConnection() {
        return instance.createConnection();
    }

}