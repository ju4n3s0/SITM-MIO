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
 * UDP datagram receiver for bus telemetry.
 * Component from deployment diagram: ReceptorDatagramas
 * 
 * Realizes: IDatagramReceiver
 * Uses: IEventBus
 */
public class ReceptorDatagramas implements IDatagramReceiver {
    
    private static final String TABLE_NAME = "mio.datagrams_history";

    //We read just the datagrams with higher id than the last processed
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
    
    public ReceptorDatagramas(IEventBus eventBus) {
        this.eventBus = eventBus;
    }
    
    @Override
    public void start() {
        // TODO: Start UDP socket listener
        // 1. Create DatagramSocket on configured port
        // 2. Start listener thread
        // 3. Parse incoming datagrams
        // 4. Publish EventoNuevoDatagrama to eventBus
        if (running) return;

        running = true;
        System.out.println("[ReceptorDatagramas] started - polling datagrams_history");

        //Simple thread that do polling everyTime
        Thread t = new Thread(this::pollLoop, "DatagramReceiverThread");
        t.setDaemon(true);
        t.start();
    }

    private void pollLoop(){
        while (running) { 
            try {
                pollOnce();
                Thread.sleep(1000L); //One second
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e){
                System.out.println("[ReceptionDatagram] Error in pollLoop" + e.getStackTrace());
            }
        }
    }

    private void pollOnce(){
        try (Connection con = ManageDatabase.gConnection();
                PreparedStatement ps = con.prepareStatement(SQL_POLL)){
            
                    ps.setLong(1, lastDatagramId);

                    try (ResultSet rs = ps.executeQuery()){
                        
                        int count = 0;
                        while (rs.next()) { 
                            EventNewDatagram event = mapRowToEvent(rs);
                            lastDatagramId = event.getDatagramId();
                            count ++;

                            //publish the event
                            eventBus.publish(event);
                            System.out.println("[ReceptorDatagrams] published " + event);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getStackTrace());
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    //Here we are mapping the datagrams_history to the EventNewDatagram
    //We also convert the gps_x and gps_y to lat/lon making a a division with 1e7 because they was to big
    private EventNewDatagram mapRowToEvent(ResultSet rs) throws Exception{

        long datagramId = rs.getLong("datagram_id");
        long busId = rs.getLong("bus_id");
        long lineId = rs.getLong("line_id");
        
        long gps_x = rs.getLong("gps_x");
        long gps_y = rs.getLong("gps_y");

        double latitude = gps_x / 1e7;
        double longitude = gps_y / 1e7;

        java.sql.Timestamp ts = rs.getTimestamp("event_ts");
        Instant eventTs = (ts != null) ? ts.toInstant() : Instant.EPOCH;

        return new EventNewDatagram(datagramId, busId, lineId, latitude, longitude, eventTs);

    }
    
    @Override
    public void stop() {
        running = false;
        System.out.println("[ReceptorDatagrams] stopped");
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
}
