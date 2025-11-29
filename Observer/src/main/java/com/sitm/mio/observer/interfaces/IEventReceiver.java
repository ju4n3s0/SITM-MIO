package com.sitm.mio.observer.interfaces;

/**
 * Interface for WebSocket event reception.
 * Defines the contract for receiving real-time events from ProxyCache.
 * 
 * Realized by: EventReceiver
 */
public interface IEventReceiver {
    
    /**
     * Connect to WebSocket server.
     */
    void connect();
    
    /**
     * Register handler for incoming events.
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
