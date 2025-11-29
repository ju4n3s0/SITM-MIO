package com.sitm.mio.operationcontrol.interfaces;

import java.util.List;

/**
 * Interface for WebSocket event reception.
 * Defines the contract for receiving real-time events from Observer module.
 * 
 * Connection: OperationControl.EventReceiver → WebSocket → Observer
 * 
 * Realized by: EventReceiver
 */
public interface IEventReceiver {
    
    /**
     * Connect to WebSocket server with authentication token.
     * @param token JWT token from authentication
     */
    void connect(String token);
    
    /**
     * Subscribe to specific zones after connection.
     * @param zones List of zone IDs to monitor
     */
    void subscribeToZones(List<String> zones);
    
    /**
     * Register handler for incoming bus position events.
     * @param handler Event handler callback
     */
    void onEvent(Object handler);
    
    /**
     * Disconnect from WebSocket.
     */
    void disconnect();
    
    /**
     * Check if WebSocket is connected.
     * @return true if connected, false otherwise
     */
    boolean isConnected();
}
