package com.sitm.mio.observer;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import com.sitm.mio.observer.component.ProxyClient;
import com.sitm.mio.observer.ice.ObserverAnalyticsI;
import com.sitm.mio.observer.ice.EventPublisherI;
import com.sitm.mio.observer.ice.ProxyServerEventSubscriberI;
import SITM.EventPublisherPrx;
import SITM.EventSubscriberPrx;

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
        ProxyClient proxyClient = null;
        
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
            proxyClient = new ProxyClient(proxyHost, proxyPort);
            
            // Create ICE servants
            ObserverAnalyticsI analyticsServant = new ObserverAnalyticsI(proxyClient);
            EventPublisherI eventPublisher = new EventPublisherI();
            com.sitm.mio.observer.ice.DataCenterEventSubscriberI dataCenterSubscriber = 
                new com.sitm.mio.observer.ice.DataCenterEventSubscriberI(eventPublisher);
            
            // Add servants to adapter
            adapter.add(analyticsServant, Util.stringToIdentity("Analytics"));
            System.out.println("Analytics servant registered");
            
            adapter.add(eventPublisher, Util.stringToIdentity("EventPublisher"));
            System.out.println("EventPublisher servant registered");
            
            ObjectPrx subscriberObj = adapter.add(dataCenterSubscriber, Util.stringToIdentity("DataCenterSubscriber"));
            System.out.println("DataCenterSubscriber servant registered");
            
            // Activate adapter
            adapter.activate();
            System.out.println("ObjectAdapter activated");
            
            System.out.println();
            // Subscribe to DataCenter's enriched datagram events
            String dataCenterEndpoint = "tcp -h localhost -p 10003";  // DataCenter ICE port
            ObjectPrx dataCenterPublisherBase = communicator.stringToProxy("DataCenterEventPublisher:" + dataCenterEndpoint);
            SITM.DataCenterEventPublisherPrx dataCenterPublisher = SITM.DataCenterEventPublisherPrx.checkedCast(dataCenterPublisherBase);
            
            if (dataCenterPublisher != null) {
                // Create DataCenter event subscriber
                SITM.DataCenterEventSubscriberPrx dataCenterSubscriberPrx = 
                    SITM.DataCenterEventSubscriberPrx.uncheckedCast(subscriberObj);
                dataCenterPublisher.subscribe(dataCenterSubscriberPrx);
                System.out.println("✅ Subscribed to DataCenter enriched datagram events");
                System.out.println("  DataCenter endpoint: " + dataCenterEndpoint);
            } else {
                System.err.println("❌ Failed to connect to DataCenter EventPublisher");
            }
            
            // Note: ProxyServer subscription removed - Observer gets data directly from DataCenter
            String subscriptionId = null;
            EventPublisherPrx proxyServerEventPublisher = null;
            
            System.out.println();
            System.out.println("========================================");
            System.out.println("  Observer System Ready");
            System.out.println("========================================");
            System.out.println();
            System.out.println("ICE Server Endpoints:");
            System.out.println("  Analytics:tcp -h localhost -p " + observerPort);
            System.out.println("  EventPublisher:tcp -h localhost -p " + observerPort);
            System.out.println();
            System.out.println("Event Flow:");
            System.out.println("  ProxyServer → Observer → OperationControl");
            System.out.println();
            System.out.println("Waiting for events from ProxyServer...");
            System.out.println("Press Ctrl+C to stop");
            System.out.println();
            
            // Add shutdown hook
            final String finalSubscriptionId = subscriptionId;
            final EventPublisherPrx finalProxyServerPublisher = proxyServerEventPublisher;
            final ProxyClient finalProxyClient = proxyClient;
            final Communicator finalCommunicator = communicator;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println();
                System.out.println("Shutting down Observer...");
                
                // Unsubscribe from ProxyServer
                if (finalProxyServerPublisher != null && finalSubscriptionId != null) {
                    try {
                        finalProxyServerPublisher.unsubscribe(finalSubscriptionId);
                        System.out.println("Unsubscribed from ProxyServer events");
                    } catch (Exception e) {
                        System.err.println("Error unsubscribing: " + e.getMessage());
                    }
                }
                
                if (finalProxyClient != null) {
                    finalProxyClient.shutdown();
                }
                if (finalCommunicator != null) {
                    finalCommunicator.destroy();
                }
                System.out.println("Observer shutdown complete");
            }));
            


            System.out.println("Observer is now waiting for events from ProxyServer...");
            System.out.println();
            
            // TODO: Implement event listener from ProxyServer
            // For now, just keep the server running
            communicator.waitForShutdown();
            
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
