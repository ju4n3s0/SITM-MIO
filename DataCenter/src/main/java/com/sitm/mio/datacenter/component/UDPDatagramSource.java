package com.sitm.mio.datacenter.component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.Instant;

import com.sitm.mio.datacenter.interfaces.IDatagramSource;
import com.sitm.mio.datacenter.interfaces.IEventBus;
import com.sitm.mio.datacenter.model.EventNewDatagram;

/**
 * Real-time datagram source - listens to UDP datagrams from BusSimulator.
 * Strategy implementation for receiving live telemetry data.
 * 
 * Expected datagram format (comma-separated):
 * datagram_id,bus_id,line_id,gps_x,gps_y,timestamp
 */
public class UDPDatagramSource implements IDatagramSource {
    
    private final IEventBus eventBus;
    private final int udpPort;
    private volatile boolean running = false;
    private DatagramSocket socket;
    
    public UDPDatagramSource(IEventBus eventBus, int udpPort) {
        this.eventBus = eventBus;
        this.udpPort = udpPort;
    }
    
    @Override
    public void start() {
        if (running) return;
        
        running = true;
        System.out.println("[UDPDatagramSource] Starting UDP listener on port " + udpPort);
        
        Thread t = new Thread(this::listenLoop, "UDPDatagramSourceThread");
        t.setDaemon(true);
        t.start();
    }
    
    private void listenLoop() {
        try {
            socket = new DatagramSocket(udpPort);
            System.out.println("[UDPDatagramSource] UDP socket bound to port " + udpPort);
            
            byte[] buffer = new byte[1024];
            
            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    
                    String data = new String(packet.getData(), 0, packet.getLength());
                    EventNewDatagram event = parseDatagram(data);
                    
                    if (event != null) {
                        eventBus.publish(event);
                        System.out.println("[UDPDatagramSource] Published datagram #" + event.getDatagramId());
                    }
                    
                } catch (Exception e) {
                    if (running) {
                        System.err.println("[UDPDatagramSource] Error receiving datagram: " + e.getMessage());
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("[UDPDatagramSource] Error starting UDP socket: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
    
    /**
     * Parse UDP datagram string into EventNewDatagram.
     * Format: datagram_id,bus_id,line_id,gps_x,gps_y,timestamp
     */
    private EventNewDatagram parseDatagram(String data) {
        try {
            String[] parts = data.trim().split(",");
            if (parts.length < 6) {
                System.err.println("[UDPDatagramSource] Invalid datagram format: " + data);
                return null;
            }
            
            long datagramId = Long.parseLong(parts[0]);
            long busId = Long.parseLong(parts[1]);
            long lineId = Long.parseLong(parts[2]);
            long gpsX = Long.parseLong(parts[3]);
            long gpsY = Long.parseLong(parts[4]);
            long timestamp = Long.parseLong(parts[5]);
            
            // Convert GPS coordinates (stored as integers * 1e7)
            // NOTE: gps_x = latitude, gps_y = longitude
            double latitude = gpsX / 1e7;
            double longitude = gpsY / 1e7;
            
            Instant eventTs = Instant.ofEpochMilli(timestamp);
            
            return new EventNewDatagram(datagramId, busId, lineId, latitude, longitude, eventTs);
            
        } catch (Exception e) {
            System.err.println("[UDPDatagramSource] Error parsing datagram: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("[UDPDatagramSource] Stopped");
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public String getSourceType() {
        return "UDP (Real-time)";
    }
}
