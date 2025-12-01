package com.sitm.mio.citizen.proxyserver;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import SITM.CitizenInformation;
import SITM.ProxyServerPrx;
import com.sitm.mio.citizen.config.ConfigLoader;

/**
 * ICE client for communicating with the remote ProxyServer service.
 * 
 * This replaces the HTTP-based ProxyServerClient with ICE RPC.
 * 
 * Features:
 * - ICE RPC for efficient binary communication
 * - Automatic connection management
 * - Type-safe remote method calls
 * - Built-in retry and timeout handling
 */
public class ProxyServerClientICE {
    
    private final Communicator communicator;
    private final ProxyServerPrx proxyServer;
    private final String proxyServerEndpoint;
    
    public ProxyServerClientICE() {
        // Load configuration
        String host = ConfigLoader.getProxyServerHost();
        int port = ConfigLoader.getProxyServerPort();
        this.proxyServerEndpoint = String.format("ProxyServer:tcp -h %s -p %d", host, port);
        
        System.out.println("ProxyServerClientICE initializing...");
        System.out.println("  Endpoint: " + proxyServerEndpoint);
        
        try {
            // Initialize ICE communicator
            this.communicator = Util.initialize();
            
            // Create proxy to ProxyServer
            ObjectPrx base = communicator.stringToProxy(proxyServerEndpoint);
            this.proxyServer = ProxyServerPrx.checkedCast(base);
            
            if (this.proxyServer == null) {
                throw new RuntimeException("Invalid proxy: " + proxyServerEndpoint);
            }
            
            System.out.println("Connected to ProxyServer via ICE");
            
        } catch (Exception e) {
            System.err.println("Failed to initialize ICE client: " + e.getMessage());
            throw new RuntimeException("ICE initialization failed", e);
        }
    }
    
    /**
     * Get citizen travel time information from ProxyServer.
     * 
     * @param originId Origin stop ID
     * @param destinationId Destination stop ID
     * @return CitizenInformation with travel time details
     */
    public CitizenInformation getCitizenInformation(long originId, long destinationId) {
        System.out.println("ProxyServerClientICE: Querying via ICE RPC");
        System.out.println("  Origin: " + originId);
        System.out.println("  Destination: " + destinationId);
        
        try {
            // Make ICE RPC call
            CitizenInformation info = proxyServer.getCitizenInformation(originId, destinationId);
            
            System.out.println("Response received: " + info.message);
            return info;
            
        } catch (Exception e) {
            System.err.println("ICE RPC call failed: " + e.getMessage());
            e.printStackTrace();
            
            // Return error information
            CitizenInformation errorInfo = new CitizenInformation();
            errorInfo.message = "Error: Unable to connect to ProxyServer - " + e.getMessage();
            return errorInfo;
        }
    }
    
    /**
     * Shutdown the ICE communicator.
     * Should be called when the client is no longer needed.
     */
    public void shutdown() {
        if (communicator != null) {
            try {
                communicator.destroy();
                System.out.println("ProxyServerClientICE shut down");
            } catch (Exception e) {
                System.err.println("Error shutting down ICE: " + e.getMessage());
            }
        }
    }
}
