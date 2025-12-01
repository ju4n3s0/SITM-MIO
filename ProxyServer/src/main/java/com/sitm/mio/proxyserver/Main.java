package com.sitm.mio.proxyserver;

import com.zeroc.Ice.*;
import com.sitm.mio.proxyserver.analytics.AnalyticsService;
import com.sitm.mio.proxyserver.cache.CacheManager;
import com.sitm.mio.proxyserver.ice.AnalyticsI;
import com.sitm.mio.proxyserver.ice.HealthCheckI;
import com.sitm.mio.proxyserver.ice.ProxyServerI;
import com.sitm.mio.proxyserver.service.RequestRouter;

/**
 * Main entry point for the ProxyServer application (ICE version).
 * 
 * ProxyServer acts as an intermediary between:
 * - Citizen module (queries travel time information via ICE RPC)
 * - DataCenter module (provides route calculations via ICE RPC)
 * - Observer module (monitors system via ICE RPC)
 * 
 * Features:
 * - ICE RPC for Citizen requests
 * - ICE RPC for Observer analytics
 * - Cache with TTL for performance
 * - Event streaming support (ICE events)
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   SITM-MIO ProxyServer Starting...    ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();
        
        Communicator communicator = null;
        
        try {
            // Initialize ICE communicator
            communicator = Util.initialize(args);
            System.out.println("ICE Communicator initialized");
            
            // Create object adapter
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "ProxyServerAdapter", "tcp -h 0.0.0.0 -p 10000"
            );
            System.out.println("ICE ObjectAdapter created on port 10000");
            
            System.out.println();
            System.out.println("Initializing business logic...");
            
            // Initialize business logic components
            RequestRouter requestRouter = new RequestRouter();
            System.out.println("RequestRouter initialized");
            
            CacheManager cacheManager = requestRouter.getCacheManager();
            AnalyticsService analyticsService = new AnalyticsService(cacheManager);
            System.out.println("AnalyticsService initialized");
            
            System.out.println();
            System.out.println("Creating ICE servants...");
            
            // Create ICE servants
            ProxyServerI proxyServerServant = new ProxyServerI(requestRouter);
            AnalyticsI analyticsServant = new AnalyticsI(analyticsService);
            HealthCheckI healthCheckServant = new HealthCheckI();
            
            // Add servants to adapter
            adapter.add(proxyServerServant, Util.stringToIdentity("ProxyServer"));
            System.out.println("ProxyServer servant registered");
            
            adapter.add(analyticsServant, Util.stringToIdentity("Analytics"));
            System.out.println("Analytics servant registered");
            
            adapter.add(healthCheckServant, Util.stringToIdentity("HealthCheck"));
            System.out.println("HealthCheck servant registered");
            
            // Activate adapter
            adapter.activate();
            System.out.println("ObjectAdapter activated");
            
            System.out.println();
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║   ProxyServer Ready!                  ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println();
            System.out.println("ICE Endpoints:");
            System.out.println("  - ProxyServer:tcp -h localhost -p 10000");
            System.out.println("  - Analytics:tcp -h localhost -p 10000");
            System.out.println("  - HealthCheck:tcp -h localhost -p 10000");
            System.out.println();
            System.out.println("Waiting for requests...");
            System.out.println("Press Ctrl+C to stop.");
            System.out.println();
            
            // Wait for shutdown
            communicator.waitForShutdown();
            
        } catch (java.lang.Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (communicator != null) {
                try {
                    communicator.destroy();
                    System.out.println("\nProxyServer shut down cleanly.");
                } catch (java.lang.Exception e) {
                    System.err.println("Error during shutdown: " + e.getMessage());
                }
            }
        }
    }
}