package com.sitm.mio.operationcontrol;

import com.sitm.mio.operationcontrol.component.Controller;
import com.sitm.mio.operationcontrol.component.ObserverAnalyticsClient;
import com.sitm.mio.operationcontrol.component.ProxyClient;
import com.sitm.mio.operationcontrol.component.DataAccessLayer;
import com.sitm.mio.operationcontrol.component.TravelTimeDataInserter;
import com.sitm.mio.operationcontrol.component.CalculateAverageTime;
import com.sitm.mio.operationcontrol.component.CalculateAverageSpeed;
import com.sitm.mio.operationcontrol.ice.EventSubscriberI;
import com.sitm.mio.operationcontrol.ui.OperationControlUI;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import SITM.EventPublisherPrx;
import SITM.EventSubscriberPrx;
import SITM.SystemStatistics;
import SITM.ZoneStatistics;
import SITM.HistoricalData;
import javax.swing.SwingUtilities;

/**
 * Main entry point for Operation Control System (ICE version).
 * Monitors both ProxyServer and Observer via ICE RPC.
 */
public class Main {
    
    private static String currentSubscriptionId = null;
    
    /**
     * Resubscribe to Observer with zone filtering.
     * Unsubscribes from the current subscription and creates a new one with zone filter.
     */
    private static synchronized void resubscribeWithZone(EventPublisherPrx eventPublisher, 
                                                         EventSubscriberPrx subscriberPrx, 
                                                         String zone,
                                                         EventSubscriberI subscriberServant) {
        try {
            System.out.println();
            System.out.println("========================================");
            System.out.println("  RESUBSCRIBING WITH ZONE FILTER");
            System.out.println("========================================");
            
            // Disable filtering during resubscription
            subscriberServant.disableFiltering();
            
            // Unsubscribe from current subscription
            if (currentSubscriptionId != null) {
                System.out.println("Unsubscribing from current subscription: " + currentSubscriptionId);
                eventPublisher.unsubscribe(currentSubscriptionId);
            }
            
            // Subscribe with zone filtering
            String[] zones = {zone};
            currentSubscriptionId = eventPublisher.subscribeWithZones(subscriberPrx, zones);
            
            System.out.println("✅ Resubscribed with zone filter!");
            System.out.println("  New Subscription ID: " + currentSubscriptionId);
            System.out.println("  Filtered Zone: " + zone);
            System.out.println("  Waiting for server-side filtering to stabilize...");
            System.out.println("========================================");
            System.out.println();
            
            // Wait a moment for the subscription to fully activate, then enable filtering
            new Thread(() -> {
                try {
                    Thread.sleep(500); // 500ms delay to ensure subscription is active
                    subscriberServant.enableFiltering();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
            
        } catch (Exception e) {
            System.err.println("❌ Failed to resubscribe with zone filter: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
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
        ProxyClient proxyClient = null;
        ObserverAnalyticsClient observerClient = null;
        
        try {
            // Initialize ICE communicator
            communicator = Util.initialize(args);
            System.out.println("ICE Communicator initialized");
            
            // Create object adapter for OperationControl's callback interface
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "OperationControlAdapter", "tcp -h 0.0.0.0 -p " + controlPort
            );
            System.out.println("ICE ObjectAdapter created on port " + controlPort);
            
            // Initialize calculation components
            System.out.println("Initializing calculation components...");
            ObserverAnalyticsClient tempObserverClient = new ObserverAnalyticsClient(observerHost, observerPort);
            DataAccessLayer dataAccess = new DataAccessLayer(tempObserverClient);
            TravelTimeDataInserter dataInserter = new TravelTimeDataInserter(proxyHost, proxyPort);
            
            CalculateAverageTime travelTimeCalculator = new CalculateAverageTime(
                null, null, dataAccess, dataInserter
            );
            CalculateAverageSpeed speedCalculator = new CalculateAverageSpeed(
                null, null, dataAccess
            );
            System.out.println("Calculation components initialized");
            
            // Create EventSubscriber servant with calculators
            EventSubscriberI subscriberServant = new EventSubscriberI(travelTimeCalculator, speedCalculator);
            
            // Add servant to adapter
            ObjectPrx subscriberObj = adapter.add(subscriberServant, Util.stringToIdentity("EventSubscriber"));
            System.out.println("EventSubscriber servant registered");
            
            // Activate adapter
            adapter.activate();
            System.out.println("ObjectAdapter activated");
            
            System.out.println();
            System.out.println("Initializing connections...");
            
            // Initialize ICE clients
            proxyClient = new ProxyClient(proxyHost, proxyPort);
            observerClient = new ObserverAnalyticsClient(observerHost, observerPort);
            
            // Subscribe to Observer's events (initially without zone filter)
            String observerEndpoint = String.format("tcp -h %s -p %d", observerHost, observerPort);
            ObjectPrx publisherBase = communicator.stringToProxy("EventPublisher:" + observerEndpoint);
            EventPublisherPrx eventPublisher = EventPublisherPrx.checkedCast(publisherBase);
            
            if (eventPublisher != null) {
                EventSubscriberPrx subscriberPrx = EventSubscriberPrx.uncheckedCast(subscriberObj);
                currentSubscriptionId = eventPublisher.subscribe(subscriberPrx);
                System.out.println("Subscribed to Observer events (no filter)");
                System.out.println("  Subscription ID: " + currentSubscriptionId);
                System.out.println("  Will resubscribe with zone filter after login");
            } else {
                System.err.println("Failed to connect to Observer EventPublisher");
            }
            
            final EventPublisherPrx finalEventPublisher = eventPublisher;
            final ProxyClient finalProxyClient = proxyClient;
            final ObserverAnalyticsClient finalObserverClient = observerClient;
            final Communicator finalCommunicator = communicator;
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println();
                System.out.println("Shutting down Operation Control...");
                
                // Unsubscribe from events
                if (finalEventPublisher != null && currentSubscriptionId != null) {
                    try {
                        finalEventPublisher.unsubscribe(currentSubscriptionId);
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
            System.out.println("Launching UI...");
            System.out.println();
            
            // Launch UI on Event Dispatch Thread with ProxyClient
            final EventSubscriberI finalSubscriberServant = subscriberServant;
            final EventSubscriberPrx finalSubscriberPrx = EventSubscriberPrx.uncheckedCast(subscriberObj);
            
            SwingUtilities.invokeLater(() -> {
                OperationControlUI ui = new OperationControlUI(finalProxyClient);
                ui.setVisible(true);
                
                // Connect UI to EventSubscriberI for real-time updates and zone filtering
                finalSubscriberServant.setUI(ui);
                ui.setEventSubscriber(finalSubscriberServant);
                
                // Set callback for zone-based resubscription
                ui.setOnZoneAssignedCallback(() -> {
                    String assignedZone = ui.getAssignedZone();
                    if (assignedZone != null && finalEventPublisher != null) {
                        resubscribeWithZone(finalEventPublisher, finalSubscriberPrx, assignedZone, finalSubscriberServant);
                    }
                });
                
                System.out.println("UI launched successfully");
                System.out.println("Real-time updates enabled");
                System.out.println("Server-side zone filtering will activate after login");
            });
            
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
