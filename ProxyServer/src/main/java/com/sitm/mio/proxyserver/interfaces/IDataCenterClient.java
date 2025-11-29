package com.sitm.mio.proxyserver.interfaces;

/**
 * Interface for communication with DataCenter backend.
 * Handles data queries and operator authentication.
 * 
 * Realized by: DataCenterClient
 */
public interface IDataCenterClient {
    
    /**
     * Authenticate operator credentials with DataCenter.
     * 
     * @param username Operator username
     * @param password Operator password
     * @return Operator data with assigned zones, or null if invalid
     */
    Object authenticateOperator(String username, String password);
    
    /**
     * Get system-wide statistics from DataCenter.
     * 
     * @return System statistics
     */
    Object getSystemStatistics();
    
    /**
     * Get statistics for a specific zone.
     * 
     * @param zoneId Zone identifier
     * @return Zone statistics including arc data
     */
    Object getZoneStatistics(String zoneId);
    
    /**
     * Get historical data for analytics.
     * 
     * @param timeRange Time range specification
     * @return Historical data
     */
    Object getHistoricalData(String timeRange);
    
    /**
     * Check if DataCenter is reachable.
     * 
     * @return true if DataCenter is available
     */
    boolean isDataCenterAvailable();
}
