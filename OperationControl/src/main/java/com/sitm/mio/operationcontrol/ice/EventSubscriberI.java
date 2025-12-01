package com.sitm.mio.operationcontrol.ice;

import com.zeroc.Ice.Current;
import SITM.Event;
import SITM.EventSubscriber;
import com.sitm.mio.operationcontrol.component.CalculateAverageTime;
import com.sitm.mio.operationcontrol.component.CalculateAverageSpeed;
import com.sitm.mio.operationcontrol.ui.OperationControlUI;

/**
 * ICE servant implementation for EventSubscriber interface.
 * OperationControl (Sistema Controlador de Operación) receives event notifications from Observer.
 * 
 * Implements the Observer pattern where:
 * - OperationControl is the Observer (EventSubscriber)
 * - Observer is the Subject (EventPublisher)
 * 
 * When datagrams are received, automatically triggers travel time and speed calculations.
 */
public class EventSubscriberI implements EventSubscriber {
    
    private final CalculateAverageTime travelTimeCalculator;
    private final CalculateAverageSpeed speedCalculator;
    private OperationControlUI ui;
    private String assignedZone;
    private int datagramCount = 0;
    private int filteredCount = 0;
    private long lastUpdateTime = 0;
    private volatile boolean filteringActive = false; // Start disabled, enable after UI ready or login
    
    public EventSubscriberI(CalculateAverageTime travelTimeCalculator, CalculateAverageSpeed speedCalculator) {
        this.travelTimeCalculator = travelTimeCalculator;
        this.speedCalculator = speedCalculator;
        System.out.println("EventSubscriberI created - ready to receive notifications and trigger calculations");
    }
    
    /**
     * Set the UI for real-time updates.
     * Enables filtering once UI is connected (before login, shows all zones).
     */
    public void setUI(OperationControlUI ui) {
        this.ui = ui;
        this.filteringActive = true; // Enable filtering once UI is ready
        System.out.println("EventSubscriberI: UI connected for real-time updates");
        System.out.println("EventSubscriberI: Filtering ENABLED (showing all zones until login)");
    }
    
    /**
     * Set the assigned zone for filtering events.
     * Only events from this zone will be processed and displayed.
     */
    public void setAssignedZone(String zone) {
        this.assignedZone = zone;
        System.out.println("EventSubscriberI: Zone filter set to " + zone);
    }
    
    /**
     * Disable filtering temporarily during resubscription.
     * This prevents showing unfiltered events during the transition.
     */
    public void disableFiltering() {
        this.filteringActive = false;
        System.out.println("EventSubscriberI: Filtering PAUSED for resubscription");
    }
    
    /**
     * Enable filtering after resubscription is complete.
     * This allows events to be processed and displayed again.
     */
    public void enableFiltering() {
        this.filteringActive = true;
        System.out.println("EventSubscriberI: Server-side filtering now ACTIVE");
    }
    
    @Override
    public void onEvent(Event event, Current current) {
        datagramCount++;
        
        // Skip events until server-side filtering is active
        if (!filteringActive) {
            System.out.println("[SKIPPED] Event #" + datagramCount + " - filtering not active (UI connected: " + (ui != null) + ")");
            return;
        }
        
        System.out.println("========================================");
        System.out.println("EVENT #" + datagramCount + " RECEIVED FROM OBSERVER");
        System.out.println("========================================");
        System.out.println("Type: " + event.type);
        System.out.println("Source: " + event.source);
        System.out.println("Message: " + event.message);
        System.out.println("Timestamp: " + event.timestamp);
        System.out.println("========================================");
        
        // Update UI with real-time data
        if (ui != null) {
            long currentTime = System.currentTimeMillis();
            // Update UI every 2 seconds to avoid overwhelming it
            if (currentTime - lastUpdateTime > 2000) {
                System.out.println("[UI] Updating real-time analytics...");
                updateUI(event);
                lastUpdateTime = currentTime;
            } else {
                System.out.println("[UI] Skipping update - too soon (last: " + (currentTime - lastUpdateTime) + "ms ago)");
            }
        } else {
            System.out.println("[UI] UI not connected - cannot update");
        }
        
        // Trigger automatic calculations every 10 datagrams
        if (datagramCount % 10 == 0) {
            System.out.println("🔄 Triggering automatic travel time calculation...");
            triggerCalculations();
        }
        
        System.out.println();
    }
    
    /**
     * Update the UI with real-time event data.
     */
    private void updateUI(Event event) {
        if (ui == null) return;
        
        try {
            // Create a simple statistics display
            javax.swing.SwingUtilities.invokeLater(() -> {
                String info = String.format(
                    "<html><b>Real-Time Data</b><br>" +
                    "Events Received: %d<br>" +
                    "Last Event: %s<br>" +
                    "Source: %s<br>" +
                    "Timestamp: %s</html>",
                    datagramCount,
                    event.message,
                    event.source,
                    new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(event.timestamp))
                );
                
                // Display in stats panel (you'll need to add this method to OperationControlUI)
                ui.updateRealTimeInfo(info);
            });
        } catch (Exception e) {
            System.err.println("[EventSubscriberI] Error updating UI: " + e.getMessage());
        }
    }
    
    @Override
    public void onEvents(Event[] events, Current current) {
        System.out.println("========================================");
        System.out.println("BATCH OF " + events.length + " EVENTS RECEIVED");
        System.out.println("========================================");
        
        for (Event event : events) {
            datagramCount++;
            System.out.println("- " + event.type + ": " + event.message);
        }
        
        // Trigger calculations after batch
        System.out.println("🔄 Triggering automatic calculations after batch...");
        triggerCalculations();
        
        System.out.println("========================================");
        System.out.println();
    }
    
    /**
     * Trigger automatic travel time and speed calculations.
     * This simulates real calculations - in production, would use actual stop pairs and zones.
     */
    private void triggerCalculations() {
        try {
            // Example: Calculate travel time for a common route
            // In production, this would be based on actual datagram data
            long originStop = 500100;
            long destinationStop = 500200;
            int timeWindow = 60; // minutes
            String zoneId = assignedZone != null ? assignedZone : "UNKNOWN";
            
            System.out.println("  📊 Calculating travel time: " + originStop + " -> " + destinationStop + " (Zone: " + zoneId + ")");
            
            if (travelTimeCalculator != null) {
                // Run calculation in background thread to avoid blocking event processing
                new Thread(() -> {
                    try {
                        java.util.Map<String, Object> result = 
                            travelTimeCalculator.calculateAverageTime(originStop, destinationStop, timeWindow);
                        
                        if ("SUCCESS".equals(result.get("status"))) {
                            double avgTime = ((Number) result.get("averageTimeMinutes")).doubleValue();
                            int sampleCount = ((Number) result.get("sampleCount")).intValue();
                            System.out.println("  ✅ Travel time calculated: " + String.format("%.1f", avgTime) + " minutes (" + sampleCount + " samples)");
                            System.out.println("  📤 Submitting to database via ProxyServer...");
                            
                            // The CalculateAverageTime should automatically submit via TravelTimeDataInserter
                            // But let's verify it's being called
                        } else {
                            System.out.println("  ⚠️ Calculation status: " + result.get("status"));
                        }
                    } catch (Exception e) {
                        System.err.println("  ❌ Calculation error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }).start();
            } else {
                System.out.println("  ⚠️ Travel time calculator not initialized");
            }
            
        } catch (Exception e) {
            System.err.println("Error triggering calculations: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
