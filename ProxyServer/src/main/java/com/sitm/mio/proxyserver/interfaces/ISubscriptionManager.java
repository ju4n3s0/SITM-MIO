package com.sitm.mio.proxyserver.interfaces;

import java.util.List;

/**
 * Interface for WebSocket subscription management.
 * Manages real-time event distribution to subscribed clients.
 * 
 * Realized by: SubscriptionManager
 */
public interface ISubscriptionManager {
    
    /**
     * Start the WebSocket server.
     */
    void start();
    
    /**
     * Stop the WebSocket server.
     */
    void stop();
    
    /**
     * Register a client subscription for specific zones.
     * 
     * @param sessionId WebSocket session identifier
     * @param zones List of zone IDs to subscribe to
     */
    void subscribe(String sessionId, List<String> zones);
    
    /**
     * Unsubscribe a client from all zones.
     * 
     * @param sessionId WebSocket session identifier
     */
    void unsubscribe(String sessionId);
    
    /**
     * Broadcast an event to all subscribed clients in a zone.
     * 
     * @param zoneId Zone identifier
     * @param event Event object to broadcast
     */
    void broadcastToZone(String zoneId, Object event);
    
    /**
     * Broadcast an event to all connected clients (system-wide).
     * 
     * @param event Event object to broadcast
     */
    void broadcastToAll(Object event);
    
    /**
     * Check if WebSocket server is running.
     * 
     * @return true if running, false otherwise
     */
    boolean isRunning();
}
