package com.sitm.mio.observer;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import com.sitm.mio.observer.component.ProxyClientICE;
import com.sitm.mio.observer.ice.ObserverAnalyticsI;
import com.sitm.mio.observer.ice.EventPublisherI;
import SITM.SystemStatistics;
import SITM.Event;
import SITM.EventType;

/**
 * Main entry point for the Observer System (ICE version).
 * 
 * Observer now acts as both:
 * - ICE Client: Queries ProxyServer for analytics data
 * - ICE Server: Exposes Analytics interface for OperationControl
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  SITM-MIO Observer System Starting");
        System.out.println("  ICE Analytics Server");
        System.out.println("========================================");
        System.out.println();
        
        // Configuration
        String proxyHost = System.getProperty("proxyserver.host", "localhost");
        int proxyPort = Integer.parseInt(System.getProperty("proxyserver.port", "10000"));
        int observerPort = Integer.parseInt(System.getProperty("observer.port", "10001"));
        
        System.out.println("Configuration:");
        System.out.println("  ProxyServer Host: " + proxyHost);
        System.out.println("  ProxyServer Port: " + proxyPort);
        System.out.println("  Observer Port: " + observerPort);
        System.out.println();
        
        Communicator communicator = null;
        ProxyClientICE proxyClient = null;
        
        try {
            // Initialize ICE communicator
            communicator = Util.initialize(args);
            System.out.println("ICE Communicator initialized");
            
            // Create object adapter for Observer's ICE server
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "ObserverAdapter", "tcp -h 0.0.0.0 -p " + observerPort
            );
            System.out.println("ICE ObjectAdapter created on port " + observerPort);
            
            System.out.println();
            System.out.println("Initializing components...");
            
            // Initialize ICE client to ProxyServer
            proxyClient = new ProxyClientICE(proxyHost, proxyPort);
            
            // Create ICE servants
            ObserverAnalyticsI analyticsServant = new ObserverAnalyticsI(proxyClient);
            EventPublisherI eventPublisher = new EventPublisherI();
            
            // Add servants to adapter
            adapter.add(analyticsServant, Util.stringToIdentity("Analytics"));
            System.out.println("Analytics servant registered");
            
            adapter.add(eventPublisher, Util.stringToIdentity("EventPublisher"));
            System.out.println("EventPublisher servant registered");
            
            // Activate adapter
            adapter.activate();
            System.out.println("ObjectAdapter activated");
            
            System.out.println();
            System.out.println("========================================");
            System.out.println("  Observer System Ready");
            System.out.println("========================================");
            System.out.println();
            System.out.println("ICE Server Endpoints:");
            System.out.println("  Analytics:tcp -h localhost -p " + observerPort);
            System.out.println("  EventPublisher:tcp -h localhost -p " + observerPort);
            System.out.println();
            System.out.println("Monitoring ProxyServer and publishing events...");
            System.out.println("Press Ctrl+C to stop");
            System.out.println();
            
            // Add shutdown hook
            final ProxyClientICE finalProxyClient = proxyClient;
            final Communicator finalCommunicator = communicator;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println();
                System.out.println("Shutting down Observer...");
                if (finalProxyClient != null) {
                    finalProxyClient.shutdown();
                }
                if (finalCommunicator != null) {
                    finalCommunicator.destroy();
                }
                System.out.println("Observer shutdown complete");
            }));
            
            // Query analytics periodically and publish events to subscribers
            while (true) {
                if (proxyClient.isServerReachable()) {
                    SystemStatistics stats = proxyClient.getSystemStatistics();
                    if (stats != null) {
                        System.out.println("ProxyServer Stats: " + stats.totalRequests + " requests, " +
                                         String.format("%.2f%%", stats.cacheHitRate * 100) + " hit rate");
                        
                        // Create event and notify subscribers
                        Event event = new Event();
                        event.type = EventType.RequestProcessed;
                        event.source = "Observer";
                        event.message = "System stats updated: " + stats.totalRequests + " total requests";
                        event.timestamp = System.currentTimeMillis();
                        
                        // Notify all subscribers (OperationControl)
                        eventPublisher.notifySubscribers(event);
                    }
                }
                
                Thread.sleep(10000);
            }
            
        } catch (InterruptedException e) {
            System.out.println("Observer interrupted");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (proxyClient != null) {
                proxyClient.shutdown();
            }
            if (communicator != null) {
                communicator.destroy();
            }
        }
    }
}
