package com.sitm.mio.datacenter.interfaces;

/**
 * Strategy interface for datagram sources.
 * Allows switching between historic (database) and real-time (UDP) sources.
 */
public interface IDatagramSource {
    
    /**
     * Start receiving datagrams from this source.
     */
    void start();
    
    /**
     * Stop receiving datagrams from this source.
     */
    void stop();
    
    /**
     * Check if the source is currently running.
     * @return true if running, false otherwise
     */
    boolean isRunning();
    
    /**
     * Get the source type for logging/debugging.
     * @return Source type name (e.g., "Database", "UDP")
     */
    String getSourceType();
}
