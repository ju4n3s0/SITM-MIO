package com.sitm.mio.observer.component;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import SITM.AnalyticsPrx;
import SITM.SystemStatistics;
import SITM.ZoneStatistics;
import SITM.HistoricalData;
import SITM.HealthCheckPrx;

/**
 * ICE RPC client for querying ProxyServer analytics.
 * Replaces HTTP REST API with ICE RPC calls.
 * 
 * Connects to ProxyServer's Analytics and HealthCheck interfaces.
 */
public class ProxyClient {
    
    private final Communicator communicator;
    private final AnalyticsPrx analytics;
    private final HealthCheckPrx healthCheck;
    private final String proxyServerEndpoint;
    
    public ProxyClient(String host, int port) {
        this.proxyServerEndpoint = String.format("tcp -h %s -p %d", host, port);
        
        System.out.println("ProxyClientICE initializing...");
        System.out.println("  Endpoint: " + proxyServerEndpoint);
        
        try {
            // Initialize ICE communicator
            this.communicator = Util.initialize();
            
            // Create proxy to Analytics interface
            ObjectPrx analyticsBase = communicator.stringToProxy("Analytics:" + proxyServerEndpoint);
            this.analytics = AnalyticsPrx.checkedCast(analyticsBase);
            
            if (this.analytics == null) {
                throw new RuntimeException("Invalid Analytics proxy");
            }
            
            // Create proxy to HealthCheck interface
            ObjectPrx healthBase = communicator.stringToProxy("HealthCheck:" + proxyServerEndpoint);
            this.healthCheck = HealthCheckPrx.checkedCast(healthBase);
            
            if (this.healthCheck == null) {
                throw new RuntimeException("Invalid HealthCheck proxy");
            }
            
            System.out.println("Connected to ProxyServer via ICE");
            
        } catch (Exception e) {
            System.err.println("Failed to initialize ICE client: " + e.getMessage());
            throw new RuntimeException("ICE initialization failed", e);
        }
    }
    
    /**
     * Query overall system statistics via ICE RPC.
     * @return SystemStatistics or null if error
     */
    public SystemStatistics getSystemStatistics() {
        try {
            System.out.println("ProxyClientICE: Querying system statistics via ICE RPC");
            SystemStatistics stats = analytics.getSystemStatistics();
            System.out.println("  System statistics received");
            return stats;
        } catch (Exception e) {
            System.err.println("  Error fetching system statistics: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Query specific zone statistics via ICE RPC.
     * @param zoneId Zone identifier
     * @return ZoneStatistics or null if error
     */
    public ZoneStatistics getZoneStatistics(String zoneId) {
        try {
            System.out.println("ProxyClientICE: Querying zone statistics for zone: " + zoneId);
            ZoneStatistics stats = analytics.getZoneStatistics(zoneId);
            System.out.println("  Zone statistics received");
            return stats;
        } catch (Exception e) {
            System.err.println("  Error fetching zone statistics: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Query historical data via ICE RPC.
     * @param timeRange Time range for historical data (e.g., "1h", "24h", "7d")
     * @return HistoricalData or null if error
     */
    public HistoricalData getHistoricalData(String timeRange) {
        try {
            System.out.println("ProxyClientICE: Querying historical data for range: " + timeRange);
            HistoricalData data = analytics.getHistoricalData(timeRange);
            System.out.println("  Historical data received");
            return data;
        } catch (Exception e) {
            System.err.println("  Error fetching historical data: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Check if ProxyServer is reachable via ICE ping.
     * @return true if server responds to ping
     */
    public boolean isServerReachable() {
        try {
            return healthCheck.ping();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get server status via ICE RPC.
     * @return Status string or null if error
     */
    public String getServerStatus() {
        try {
            return healthCheck.getStatus();
        } catch (Exception e) {
            System.err.println("Error getting server status: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Shutdown the ICE communicator.
     */
    public void shutdown() {
        if (communicator != null) {
            try {
                communicator.destroy();
                System.out.println("ProxyClientICE shut down");
            } catch (Exception e) {
                System.err.println("Error shutting down ICE: " + e.getMessage());
            }
        }
    }
}
