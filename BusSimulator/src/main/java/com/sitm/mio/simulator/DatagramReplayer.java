package com.sitm.mio.simulator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * Replays real datagrams from datagrams_history table.
 * Reads historical data and inserts it with current timestamps.
 */
public class DatagramReplayer {
    
    private static final String SELECT_DATAGRAMS = 
        "SELECT event_date_txt, bus_id, line_id, gps_x, gps_y, " +
        "       field7, field8, field9, datagram_id, field12 " +
        "FROM mio.datagrams_history " +
        "WHERE bus_id > 0 AND gps_x > 0 AND gps_y < 0 " + // Valid data only
        "ORDER BY raw_id " +
        "LIMIT ? OFFSET ?";
    
    private static final String INSERT_DATAGRAM = 
        "INSERT INTO mio.datagrams_history " +
        "(event_date_txt, bus_id, line_id, gps_x, gps_y, field7, field8, field9, datagram_id, event_ts, field12) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private Connection connection;
    private long currentOffset = 0;
    private final int batchSize;
    private long datagramIdCounter;
    
    public DatagramReplayer() throws SQLException {
        connect();
        this.batchSize = ConfigLoader.getBusCount(); // Replay N datagrams per iteration
        this.datagramIdCounter = System.currentTimeMillis(); // Start with unique IDs
        
        System.out.println("[DatagramReplayer] Initialized");
        System.out.println("  Batch size: " + batchSize + " datagrams per iteration");
    }
    
    private void connect() throws SQLException {
        String url = ConfigLoader.getDatabaseUrl();
        String user = ConfigLoader.getDatabaseUser();
        String password = ConfigLoader.getDatabasePassword();
        
        connection = DriverManager.getConnection(url, user, password);
        System.out.println("[DatagramReplayer] Connected to database: " + url);
    }
    
    /**
     * Replay the next batch of datagrams.
     * Reads from datagrams_history and inserts with current timestamp.
     * 
     * @return Number of datagrams replayed
     * @throws SQLException if database error occurs
     */
    public int replayNextBatch() throws SQLException {
        int count = 0;
        
        try (PreparedStatement selectPs = connection.prepareStatement(SELECT_DATAGRAMS)) {
            selectPs.setInt(1, batchSize);
            selectPs.setLong(2, currentOffset);
            
            try (ResultSet rs = selectPs.executeQuery()) {
                try (PreparedStatement insertPs = connection.prepareStatement(INSERT_DATAGRAM)) {
                    
                    while (rs.next()) {
                        // Read original datagram
                        String eventDateTxt = rs.getString("event_date_txt");
                        long busId = rs.getLong("bus_id");
                        int lineId = rs.getInt("line_id");
                        long gpsX = rs.getLong("gps_x");
                        long gpsY = rs.getLong("gps_y");
                        int field7 = rs.getInt("field7");
                        int field8 = rs.getInt("field8");
                        int field9 = rs.getInt("field9");
                        long originalDatagramId = rs.getLong("datagram_id");
                        int field12 = rs.getInt("field12");
                        
                        // Insert with current timestamp
                        Instant now = Instant.now();
                        insertPs.setString(1, now.toString());
                        insertPs.setLong(2, busId);
                        insertPs.setInt(3, lineId);
                        insertPs.setLong(4, gpsX);
                        insertPs.setLong(5, gpsY);
                        insertPs.setInt(6, field7);
                        insertPs.setInt(7, field8);
                        insertPs.setInt(8, field9);
                        insertPs.setLong(9, datagramIdCounter++);
                        insertPs.setTimestamp(10, Timestamp.from(now));
                        insertPs.setInt(11, field12);
                        
                        insertPs.executeUpdate();
                        count++;
                        
                        // Log details
                        double lat = gpsX / 1e7;
                        double lon = gpsY / 1e7;
                        System.out.println(String.format("  ✓ Bus %d (Line %d) at [%.6f, %.6f]", 
                            busId, lineId, lat, lon));
                    }
                }
            }
            
            // Update offset for next batch
            currentOffset += batchSize;
        }
        
        return count;
    }
    
    /**
     * Reset to beginning of datagrams.
     */
    public void reset() {
        currentOffset = 0;
        System.out.println("[DatagramReplayer] Reset to beginning");
    }
    
    /**
     * Close database connection.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DatagramReplayer] Database connection closed");
            } catch (SQLException e) {
                System.err.println("[DatagramReplayer] Error closing connection: " + e.getMessage());
            }
        }
    }
}
