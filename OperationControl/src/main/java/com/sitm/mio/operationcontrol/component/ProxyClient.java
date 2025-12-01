package com.sitm.mio.operationcontrol.component;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import SITM.AnalyticsPrx;
import SITM.HealthCheckPrx;
import SITM.SystemStatistics;
import SITM.ZoneStatistics;
import com.sitm.mio.operationcontrol.interfaces.IProxyClient;
import com.sitm.mio.operationcontrol.model.AuthenticatedOperatorData;
import com.sitm.mio.operationcontrol.model.OperatorCredentials;
import com.sitm.mio.operationcontrol.model.ZoneStatisticsResponse;

/**
 * ICE RPC client for OperationControl to query ProxyServer.
 * Connects to Analytics and HealthCheck interfaces.
 * 
 * For authentication, connects directly to DataCenter since that's where
 * the Authenticator component lives.
 */
public class ProxyClient implements IProxyClient {
    
    private final Communicator communicator;
    private final AnalyticsPrx analytics;
    private final HealthCheckPrx healthCheck;
    private final String dataCenterHost;
    private final int dataCenterPort;
    
    public ProxyClient(String host, int port) {
        this(host, port, "localhost", 10003);  // DataCenter ICE port
    }
    
    public ProxyClient(String host, int port, String dataCenterHost, int dataCenterPort) {
        this.dataCenterHost = dataCenterHost;
        this.dataCenterPort = dataCenterPort;
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
    
    @Override
    public ZoneStatisticsResponse getZoneStatistics(String zoneId) {
        try {
            ZoneStatistics iceStats = analytics.getZoneStatistics(zoneId);
            
            // Convert ICE ZoneStatistics to ZoneStatisticsResponse
            ZoneStatisticsResponse response = new ZoneStatisticsResponse();
            response.setZoneId(iceStats.zoneId);
            response.setVehicleCount(iceStats.requestCount); // Using requestCount as vehicle count
            response.setAvgSpeed(iceStats.averageResponseTime); // Using response time as speed
            response.setTimestamp(iceStats.timestamp);
            
            return response;
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
    
    @Override
    public AuthenticatedOperatorData authenticate(OperatorCredentials credentials) {
        System.out.println("[ProxyClient] Authenticating operator: " + credentials.getUsername());
        
        try {
            // Connect to DataCenter for authentication
            String dataCenterEndpoint = String.format("tcp -h %s -p %d", dataCenterHost, dataCenterPort);
            System.out.println("[ProxyClient] Connecting to DataCenter at " + dataCenterEndpoint);
            
            // Create proxy to Authenticator service
            ObjectPrx authBase = communicator.stringToProxy("Authenticator:" + dataCenterEndpoint);
            SITM.AuthenticatorPrx authenticator = SITM.AuthenticatorPrx.checkedCast(authBase);
            
            if (authenticator == null) {
                throw new RuntimeException("Invalid Authenticator proxy");
            }
            
            // Create ICE credentials struct
            SITM.OperatorCredentials iceCredentials = new SITM.OperatorCredentials();
            iceCredentials.username = credentials.getUsername();
            iceCredentials.password = credentials.getPassword();
            
            // Call authentication service
            System.out.println("[ProxyClient] Calling DataCenter authentication service...");
            SITM.OperatorAuthResult iceResult = authenticator.authenticateOperator(iceCredentials);
            
            if (iceResult == null) {
                System.err.println("[ProxyClient] Authentication failed - null result from DataCenter");
                return null;
            }
            
            // Validate ICE result has required fields
            if (iceResult.operatorId <= 0 || 
                iceResult.username == null || iceResult.username.trim().isEmpty() ||
                iceResult.token == null || iceResult.token.trim().isEmpty()) {
                System.err.println("[ProxyClient] Authentication failed - invalid result data");
                System.err.println("  Operator ID: " + iceResult.operatorId);
                System.err.println("  Username: " + iceResult.username);
                System.err.println("  Token: " + iceResult.token);
                return null;
            }
            
            // Convert ICE result to AuthenticatedOperatorData
            AuthenticatedOperatorData authData = new AuthenticatedOperatorData();
            authData.setOperatorId(String.valueOf(iceResult.operatorId));
            authData.setUsername(iceResult.username.trim());
            authData.setFullName(iceResult.username.trim()); // Use username as full name for now
            authData.setAssignedZones(java.util.Arrays.asList(iceResult.assignedZones));
            authData.setToken(iceResult.token.trim());
            
            System.out.println("[ProxyClient] Authentication successful!");
            System.out.println("  Operator ID: " + authData.getOperatorId());
            System.out.println("  Username: " + authData.getUsername());
            System.out.println("  Zones: " + authData.getAssignedZones());
            System.out.println("  Token: " + authData.getToken());
            
            return authData;
            
        } catch (Exception e) {
            System.err.println("[ProxyClient] Authentication failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public void logout() {
        System.out.println("[ProxyClient] Logging out operator");
        // TODO: Invalidate token on DataCenter
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
