package com.sitm.mio.datacenter.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;

import com.sitm.mio.datacenter.config.ManageDatabase;
import com.sitm.mio.datacenter.interfaces.IDatagramReceiver;
import com.sitm.mio.datacenter.interfaces.IEventBus;
import com.sitm.mio.datacenter.model.EventNewDatagram;

/**
 * Datagram receiver for bus telemetry data.
 * Polls the database for new datagrams and publishes them to the event bus.
 * 
 * Component from deployment diagram: DatagramReceiver (formerly ReceptorDatagramas)
 * 
 * Realizes: IDatagramReceiver
 * Uses: IEventBus
 */
public class DatagramReceiver implements IDatagramReceiver {
    
    private static final String TABLE_NAME = "mio.datagrams_history";

    // Read only datagrams with higher ID than the last processed
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
    
    public DatagramReceiver(IEventBus eventBus) {
        this.eventBus = eventBus;
    }
    
    @Override
    public void start() {
        // TODO: Start UDP socket listener
        // 1. Create DatagramSocket on configured port
        // 2. Start listener thread
        // 3. Parse incoming datagrams
        // 4. Publish EventNewDatagram to eventBus
        if (running) return;

        running = true;
        System.out.println("[DatagramReceiver] Started - polling datagrams_history table");

        // Simple thread that does polling every second
        Thread t = new Thread(this::pollLoop, "DatagramReceiverThread");
        t.setDaemon(true);
        t.start();
    }

    /**
     * Continuous polling loop that runs while the receiver is active.
     */
    private void pollLoop() {
        while (running) { 
            try {
                pollOnce();
                Thread.sleep(10000L); // Poll every second
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[DatagramReceiver] Error in poll loop: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Poll the database once for new datagrams.
     */
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

                    // Publish the event to the event bus
                    eventBus.publish(event);
                    System.out.println("[DatagramReceiver] Published datagram #" + event.getDatagramId());
                }
                
                if (count > 0) {
                    System.out.println("[DatagramReceiver] Processed " + count + " new datagrams");
                }
            } catch (Exception e) {
                System.err.println("[DatagramReceiver] Error processing result set: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("[DatagramReceiver] Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Map a database row to an EventNewDatagram.
     * Converts GPS coordinates from integer format (x1e7) to decimal degrees.
     * 
     * @param rs ResultSet positioned at a datagram row
     * @return EventNewDatagram event
     * @throws Exception if mapping fails
     */
    private EventNewDatagram mapRowToEvent(ResultSet rs) throws Exception {

        long datagramId = rs.getLong("datagram_id");
        long busId = rs.getLong("bus_id");
        long lineId = rs.getLong("line_id");
        
        long gpsX = rs.getLong("gps_x");
        long gpsY = rs.getLong("gps_y");

        // Convert GPS coordinates from integer format to decimal degrees
        // GPS coordinates are stored as integers multiplied by 1e7
        // NOTE: In database, gps_x = latitude, gps_y = longitude
        double latitude = gpsX / 1e7;   // gps_x is latitude (positive ~3.4)
        double longitude = gpsY / 1e7;  // gps_y is longitude (negative ~-76.5)

        java.sql.Timestamp ts = rs.getTimestamp("event_ts");
        Instant eventTs = (ts != null) ? ts.toInstant() : Instant.EPOCH;

        return new EventNewDatagram(datagramId, busId, lineId, latitude, longitude, eventTs);
    }
    
    @Override
    public void stop() {
        running = false;
        System.out.println("[DatagramReceiver] Stopped");
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
}
