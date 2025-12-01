package com.sitm.mio.operationcontrol.component;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import SITM.AnalyticsPrx;
import SITM.SystemStatistics;
import SITM.ZoneStatistics;
import SITM.HistoricalData;

/**
 * ICE RPC client for OperationControl to query Observer.
 * Connects to Observer's Analytics interface.
 */
public class ObserverAnalyticsClient {
    
    private final Communicator communicator;
    private final AnalyticsPrx analytics;
    
    public ObserverAnalyticsClient(String host, int port) {
        String endpoint = String.format("tcp -h %s -p %d", host, port);
        
        System.out.println("ObserverClientICE initializing...");
        System.out.println("  Endpoint: " + endpoint);
        
        try {
            this.communicator = Util.initialize();
            
            // Create proxy to Observer's Analytics
            ObjectPrx analyticsBase = communicator.stringToProxy("Analytics:" + endpoint);
            this.analytics = AnalyticsPrx.checkedCast(analyticsBase);
            
            if (this.analytics == null) {
                throw new RuntimeException("Invalid Analytics proxy");
            }
            
            System.out.println("Connected to Observer via ICE");
            
        } catch (Exception e) {
            System.err.println("Failed to initialize Observer ICE client: " + e.getMessage());
            throw new RuntimeException("Observer ICE initialization failed", e);
        }
    }
    
    public SystemStatistics getSystemStatistics() {
        try {
            return analytics.getSystemStatistics();
        } catch (Exception e) {
            System.err.println("Error getting system statistics from Observer: " + e.getMessage());
            return null;
        }
    }
    
    public ZoneStatistics getZoneStatistics(String zoneId) {
        try {
            return analytics.getZoneStatistics(zoneId);
        } catch (Exception e) {
            System.err.println("Error getting zone statistics from Observer: " + e.getMessage());
            return null;
        }
    }
    
    public HistoricalData getHistoricalData(String timeRange) {
        try {
            return analytics.getHistoricalData(timeRange);
        } catch (Exception e) {
            System.err.println("Error getting historical data from Observer: " + e.getMessage());
            return null;
        }
    }
    
    public boolean isObserverAvailable() {
        try {
            analytics.ice_ping();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void shutdown() {
        if (communicator != null) {
            try {
                communicator.destroy();
                System.out.println("ObserverClientICE shut down");
            } catch (Exception e) {
                System.err.println("Error shutting down Observer ICE: " + e.getMessage());
            }
        }
    }
}
