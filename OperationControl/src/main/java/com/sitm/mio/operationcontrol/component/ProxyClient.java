package com.sitm.mio.operationcontrol.component;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import SITM.AnalyticsPrx;
import SITM.HealthCheckPrx;
import SITM.SystemStatistics;
import SITM.ZoneStatistics;

/**
 * ICE RPC client for OperationControl to query ProxyServer.
 * Connects to Analytics and HealthCheck interfaces.
 */
public class ProxyClient {
    
    private final Communicator communicator;
    private final AnalyticsPrx analytics;
    private final HealthCheckPrx healthCheck;
    
    public ProxyClient(String host, int port) {
        String endpoint = String.format("tcp -h %s -p %d", host, port);
        
        System.out.println("ProxyClientICE initializing...");
        System.out.println("  Endpoint: " + endpoint);
        
        try {
            this.communicator = Util.initialize();
            
            // Create proxy to Analytics
            ObjectPrx analyticsBase = communicator.stringToProxy("Analytics:" + endpoint);
            this.analytics = AnalyticsPrx.checkedCast(analyticsBase);
            
            if (this.analytics == null) {
                throw new RuntimeException("Invalid Analytics proxy");
            }
            
            // Create proxy to HealthCheck
            ObjectPrx healthBase = communicator.stringToProxy("HealthCheck:" + endpoint);
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
    
    public SystemStatistics getSystemStatistics() {
        try {
            return analytics.getSystemStatistics();
        } catch (Exception e) {
            System.err.println("Error getting system statistics: " + e.getMessage());
            return null;
        }
    }
    
    public ZoneStatistics getZoneStatistics(String zoneId) {
        try {
            return analytics.getZoneStatistics(zoneId);
        } catch (Exception e) {
            System.err.println("Error getting zone statistics: " + e.getMessage());
            return null;
        }
    }
    
    public boolean ping() {
        try {
            return healthCheck.ping();
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getStatus() {
        try {
            return healthCheck.getStatus();
        } catch (Exception e) {
            return null;
        }
    }
    
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
