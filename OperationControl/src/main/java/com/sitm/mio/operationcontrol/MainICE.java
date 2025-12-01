package com.sitm.mio.operationcontrol;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import com.sitm.mio.operationcontrol.component.ProxyClientICE;
import com.sitm.mio.operationcontrol.component.ObserverClientICE;
import com.sitm.mio.operationcontrol.ice.EventSubscriberI;
import SITM.SystemStatistics;
import SITM.ZoneStatistics;
import SITM.HistoricalData;
import SITM.EventPublisherPrx;
import SITM.EventSubscriberPrx;

/**
 * Main entry point for Operation Control System (ICE version).
 * Monitors both ProxyServer and Observer via ICE RPC.
 */
public class MainICE {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Operation Control System Starting");
        System.out.println("  ICE Event-Driven Monitor");
        System.out.println("========================================");
        System.out.println();
        
        // Configuration
        String proxyHost = System.getProperty("proxyserver.host", "localhost");
        int proxyPort = Integer.parseInt(System.getProperty("proxyserver.port", "10000"));
        String observerHost = System.getProperty("observer.host", "localhost");
        int observerPort = Integer.parseInt(System.getProperty("observer.port", "10001"));
        int controlPort = Integer.parseInt(System.getProperty("control.port", "10002"));
        
        System.out.println("Configuration:");
        System.out.println("  ProxyServer Host: " + proxyHost);
        System.out.println("  ProxyServer Port: " + proxyPort);
        System.out.println("  Observer Host: " + observerHost);
        System.out.println("  Observer Port: " + observerPort);
        System.out.println("  Control Port: " + controlPort);
        System.out.println();
        
        Communicator communicator = null;
        ProxyClientICE proxyClient = null;
        ObserverClientICE observerClient = null;
        String subscriptionId = null;
        
        try {
            // Initialize ICE communicator
            communicator = Util.initialize(args);
            System.out.println("ICE Communicator initialized");
            
            // Create object adapter for OperationControl's callback interface
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "OperationControlAdapter", "tcp -h 0.0.0.0 -p " + controlPort
            );
            System.out.println("ICE ObjectAdapter created on port " + controlPort);
            
            // Create EventSubscriber servant
            EventSubscriberI subscriberServant = new EventSubscriberI();
            
            // Add servant to adapter
            ObjectPrx subscriberObj = adapter.add(subscriberServant, Util.stringToIdentity("EventSubscriber"));
            System.out.println("EventSubscriber servant registered");
            
            // Activate adapter
            adapter.activate();
            System.out.println("ObjectAdapter activated");
            
            System.out.println();
            System.out.println("Initializing connections...");
            
            // Initialize ICE clients
            proxyClient = new ProxyClientICE(proxyHost, proxyPort);
            observerClient = new ObserverClientICE(observerHost, observerPort);
            
            // Subscribe to Observer's events
            String observerEndpoint = String.format("tcp -h %s -p %d", observerHost, observerPort);
            ObjectPrx publisherBase = communicator.stringToProxy("EventPublisher:" + observerEndpoint);
            EventPublisherPrx eventPublisher = EventPublisherPrx.checkedCast(publisherBase);
            
            if (eventPublisher != null) {
                EventSubscriberPrx subscriberPrx = EventSubscriberPrx.uncheckedCast(subscriberObj);
                subscriptionId = eventPublisher.subscribe(subscriberPrx);
                System.out.println("Subscribed to Observer events");
                System.out.println("  Subscription ID: " + subscriptionId);
            } else {
                System.err.println("Failed to connect to Observer EventPublisher");
            }
            
            final String finalSubscriptionId = subscriptionId;
            final EventPublisherPrx finalEventPublisher = eventPublisher;
            final ProxyClientICE finalProxyClient = proxyClient;
            final ObserverClientICE finalObserverClient = observerClient;
            final Communicator finalCommunicator = communicator;
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println();
                System.out.println("Shutting down Operation Control...");
                
                // Unsubscribe from events
                if (finalEventPublisher != null && finalSubscriptionId != null) {
                    try {
                        finalEventPublisher.unsubscribe(finalSubscriptionId);
                        System.out.println("Unsubscribed from Observer events");
                    } catch (Exception e) {
                        System.err.println("Error unsubscribing: " + e.getMessage());
                    }
                }
                
                if (finalProxyClient != null) {
                    finalProxyClient.shutdown();
                }
                if (finalObserverClient != null) {
                    finalObserverClient.shutdown();
                }
                if (finalCommunicator != null) {
                    finalCommunicator.destroy();
                }
                System.out.println("Operation Control shutdown complete");
            }));
        
            System.out.println();
            System.out.println("========================================");
            System.out.println("  Operation Control Ready");
            System.out.println("========================================");
            System.out.println();
            System.out.println("Listening for events from Observer...");
            System.out.println("Press Ctrl+C to stop");
            System.out.println();
            
            // Keep application running to receive events
            communicator.waitForShutdown();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (proxyClient != null) {
                proxyClient.shutdown();
            }
            if (observerClient != null) {
                observerClient.shutdown();
            }
            if (communicator != null) {
                communicator.destroy();
            }
        }
    }
}
