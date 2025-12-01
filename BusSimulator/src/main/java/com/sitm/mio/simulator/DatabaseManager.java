package com.sitm.mio.simulator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Manages database connection and datagram insertion.
 */
public class DatabaseManager {
    
    private static final String INSERT_DATAGRAM = 
        "INSERT INTO mio.datagrams_history " +
        "(event_date_txt, bus_id, line_id, gps_x, gps_y, field7, field8, field9, datagram_id, event_ts, field12) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private Connection connection;
    private long datagramIdCounter = 1000L;
    
    public DatabaseManager() throws SQLException {
        connect();
    }
    
    private void connect() throws SQLException {
        String url = ConfigLoader.getDatabaseUrl();
        String user = ConfigLoader.getDatabaseUser();
        String password = ConfigLoader.getDatabasePassword();
        
        connection = DriverManager.getConnection(url, user, password);
        System.out.println("[DatabaseManager] Connected to database: " + url);
    }
    
    /**
     * Insert a bus datagram into the database.
     */
    public void insertDatagram(Bus bus) throws SQLException {
        Instant now = Instant.now();
        String eventDateTxt = now.toString();
        
        try (PreparedStatement ps = connection.prepareStatement(INSERT_DATAGRAM)) {
            ps.setString(1, eventDateTxt);
            ps.setLong(2, bus.getBusId());
            ps.setInt(3, bus.getLineId());
            ps.setLong(4, bus.getGpsX());
            ps.setLong(5, bus.getGpsY());
            ps.setInt(6, bus.getSpeed()); // field7 = speed
            ps.setInt(7, 0); // field8 (unused)
            ps.setInt(8, 0); // field9 (unused)
            ps.setLong(9, datagramIdCounter++);
            ps.setTimestamp(10, Timestamp.from(now));
            ps.setInt(11, 0); // field12 (unused)
            
            ps.executeUpdate();
        }
    }
    
    /**
     * Test database connection.
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Close database connection.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DatabaseManager] Database connection closed");
            } catch (SQLException e) {
                System.err.println("[DatabaseManager] Error closing connection: " + e.getMessage());
            }
        }
    }
}
