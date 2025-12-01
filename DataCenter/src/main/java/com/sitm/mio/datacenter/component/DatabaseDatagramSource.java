package com.sitm.mio.datacenter.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;

import com.sitm.mio.datacenter.config.ManageDatabase;
import com.sitm.mio.datacenter.interfaces.IDatagramSource;
import com.sitm.mio.datacenter.interfaces.IEventBus;
import com.sitm.mio.datacenter.model.EventNewDatagram;

/**
 * Historic datagram source - polls database for historical data.
 * Strategy implementation for reading from mio.datagrams_history table.
 */
public class DatabaseDatagramSource implements IDatagramSource {
    
    private static final String TABLE_NAME = "mio.datagrams_history";
    private static final String SQL_POLL = 
        "SELECT raw_id, event_date_txt, bus_id, line_id, gps_x, gps_y, " +
        "       datagram_id, event_ts " +
        "FROM " + TABLE_NAME + " " +
        "WHERE datagram_id > ? " +
        "ORDER BY datagram_id ASC " +
        "LIMIT 100";

    private final IEventBus eventBus;
    private volatile boolean running = false;
    private long lastDatagramId = 0L;
    private final long pollIntervalMs;
    
    public DatabaseDatagramSource(IEventBus eventBus) {
        this(eventBus, 10000L); // Default: poll every 10 seconds
    }
    
    public DatabaseDatagramSource(IEventBus eventBus, long pollIntervalMs) {
        this.eventBus = eventBus;
        this.pollIntervalMs = pollIntervalMs;
    }
    
    @Override
    public void start() {
        if (running) return;

        running = true;
        System.out.println("[DatabaseDatagramSource] Started - polling every " + pollIntervalMs + "ms");

        Thread t = new Thread(this::pollLoop, "DatabaseDatagramSourceThread");
        t.setDaemon(true);
        t.start();
    }

    private void pollLoop() {
        while (running) { 
            try {
                pollOnce();
                Thread.sleep(pollIntervalMs);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[DatabaseDatagramSource] Error in poll loop: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void pollOnce() {
        try (Connection con = ManageDatabase.gConnection();
                PreparedStatement ps = con.prepareStatement(SQL_POLL)) {
            
            ps.setLong(1, lastDatagramId);

            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) { 
                    EventNewDatagram event = mapRowToEvent(rs);
                    lastDatagramId = event.getDatagramId();
                    count++;

                    // Publish to event bus
                    eventBus.publish(event);
                    System.out.println("[DatabaseDatagramSource] Published datagram #" + event.getDatagramId());
                }

                if (count > 0) {
                    System.out.println("[DatabaseDatagramSource] Processed " + count + " new datagrams");
                }
            }
        } catch (Exception e) {
            System.err.println("[DatabaseDatagramSource] Error polling database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private EventNewDatagram mapRowToEvent(ResultSet rs) throws Exception {
        long datagramId = rs.getLong("datagram_id");
        long busId = rs.getLong("bus_id");
        long lineId = rs.getLong("line_id");
        
        long gpsX = rs.getLong("gps_x");
        long gpsY = rs.getLong("gps_y");

        // Convert GPS coordinates from integer format to decimal degrees
        // NOTE: In database, gps_x = latitude, gps_y = longitude
        double latitude = gpsX / 1e7;
        double longitude = gpsY / 1e7;

        java.sql.Timestamp ts = rs.getTimestamp("event_ts");
        Instant eventTs = (ts != null) ? ts.toInstant() : Instant.EPOCH;

        return new EventNewDatagram(datagramId, busId, lineId, latitude, longitude, eventTs);
    }
    
    @Override
    public void stop() {
        running = false;
        System.out.println("[DatabaseDatagramSource] Stopped");
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public String getSourceType() {
        return "Database (Historic)";
    }
}
