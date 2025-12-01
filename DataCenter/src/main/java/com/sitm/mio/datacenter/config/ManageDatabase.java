package com.sitm.mio.datacenter.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager for DataCenter.
 * Uses ConfigLoader to get database credentials from config.properties.
 */
public class ManageDatabase {

    /**
     * Get a database connection using configuration from ConfigLoader.
     * @return Database connection
     * @throws SQLException if connection fails
     */
    public static Connection gConnection() throws SQLException {
        String url = ConfigLoader.getDatabaseUrl();
        String user = ConfigLoader.getDatabaseUser();
        String password = ConfigLoader.getDatabasePassword();
        
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("[ManageDatabase] Connection established to: " + url);
            return conn;
        } catch (SQLException e) {
            System.err.println("[ManageDatabase] Failed to connect to database");
            System.err.println("  URL: " + url);
            System.err.println("  User: " + user);
            System.err.println("  Error: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Test database connection.
     * @return true if connection successful
     */
    public static boolean testConnection() {
        try (Connection conn = gConnection()) {
            System.out.println("Database connection test successful!");
            System.out.println("  Database: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("  Version: " + conn.getMetaData().getDatabaseProductVersion());
            System.out.println("  URL: " + conn.getMetaData().getURL());
            return true;
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

}
