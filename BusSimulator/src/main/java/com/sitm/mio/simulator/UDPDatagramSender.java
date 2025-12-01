package com.sitm.mio.simulator;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

/**
 * Sends real datagrams via UDP to DataCenter (real-time mode).
 * Reads from datagrams_history and sends via UDP instead of inserting to DB.
 */
public class UDPDatagramSender {
    
    private static final String SELECT_DATAGRAMS = 
        "SELECT event_date_txt, bus_id, line_id, gps_x, gps_y, " +
        "       field7, field8, field9, datagram_id, field12 " +
        "FROM mio.datagrams_history " +
        "WHERE bus_id > 0 AND gps_x > 0 AND gps_y < 0 " + // Valid data only
        "ORDER BY raw_id " +
        "LIMIT ? OFFSET ?";
    
    private Connection connection;
    private DatagramSocket socket;
    private InetAddress dataCenterAddress;
    private int dataCenterPort;
    private long currentOffset = 0;
    private final int batchSize;
    private long datagramIdCounter;
    
    public UDPDatagramSender(String dataCenterHost, int dataCenterPort) throws Exception {
        connect();
        this.batchSize = ConfigLoader.getBusCount();
        this.datagramIdCounter = System.currentTimeMillis();
        this.dataCenterPort = dataCenterPort;
        this.dataCenterAddress = InetAddress.getByName(dataCenterHost);
        this.socket = new DatagramSocket();
        
        System.out.println("[UDPDatagramSender] Initialized");
        System.out.println("  Target: " + dataCenterHost + ":" + dataCenterPort);
        System.out.println("  Batch size: " + batchSize + " datagrams per iteration");
    }
    
    private void connect() throws SQLException {
        String url = ConfigLoader.getDatabaseUrl();
        String user = ConfigLoader.getDatabaseUser();
        String password = ConfigLoader.getDatabasePassword();
        
        connection = DriverManager.getConnection(url, user, password);
        System.out.println("[UDPDatagramSender] Connected to database: " + url);
    }
    
    /**
     * Send the next batch of datagrams via UDP.
     * Reads from datagrams_history and sends to DataCenter.
     * 
     * @return Number of datagrams sent
     * @throws SQLException if database error occurs
     */
    public int sendNextBatch() throws Exception {
        int count = 0;
        
        try (PreparedStatement selectPs = connection.prepareStatement(SELECT_DATAGRAMS)) {
            selectPs.setInt(1, batchSize);
            selectPs.setLong(2, currentOffset);
            
            try (ResultSet rs = selectPs.executeQuery()) {
                while (rs.next()) {
                    // Read original datagram
                    long busId = rs.getLong("bus_id");
                    int lineId = rs.getInt("line_id");
                    long gpsX = rs.getLong("gps_x");
                    long gpsY = rs.getLong("gps_y");
                    
                    // Create UDP datagram
                    // Format: datagram_id,bus_id,line_id,gps_x,gps_y,timestamp
                    long timestamp = System.currentTimeMillis();
                    String data = String.format("%d,%d,%d,%d,%d,%d",
                        datagramIdCounter++, busId, lineId, gpsX, gpsY, timestamp);
                    
                    // Send via UDP
                    byte[] buffer = data.getBytes();
                    DatagramPacket packet = new DatagramPacket(
                        buffer, buffer.length, dataCenterAddress, dataCenterPort);
                    socket.send(packet);
                    
                    count++;
                    
                    // Log details
                    double lat = gpsX / 1e7;
                    double lon = gpsY / 1e7;
                    System.out.println(String.format("  ✓ Sent UDP: Bus %d (Line %d) at [%.6f, %.6f]", 
                        busId, lineId, lat, lon));
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
        System.out.println("[UDPDatagramSender] Reset to beginning");
    }
    
    /**
     * Close database connection and UDP socket.
     */
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("[UDPDatagramSender] UDP socket closed");
        }
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[UDPDatagramSender] Database connection closed");
            } catch (SQLException e) {
                System.err.println("[UDPDatagramSender] Error closing connection: " + e.getMessage());
            }
        }
    }
}
