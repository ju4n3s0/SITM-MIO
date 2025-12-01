package com.sitm.mio.observer.ice;

import com.zeroc.Ice.Current;
import SITM.Event;
import SITM.EventPublisher;
import SITM.EventSubscriberPrx;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;

/**
 * ICE servant implementation for EventPublisher interface.
 * Observer (Sistema Observador) publishes events to subscribers with zone-based filtering.
 * 
 * Implements the Observer pattern where:
 * - Observer is the Subject (EventPublisher)
 * - OperationControl is the Observer (EventSubscriber)
 * 
 * Subscribers can specify which zones they want to monitor.
 */
public class EventPublisherI implements EventPublisher {
    
    private static class SubscriberInfo {
        EventSubscriberPrx proxy;
        Set<String> zones; // Empty set means all zones
        
        SubscriberInfo(EventSubscriberPrx proxy, Set<String> zones) {
            this.proxy = proxy;
            this.zones = zones != null ? zones : new HashSet<>();
        }
    }
    
    private final Map<String, SubscriberInfo> subscribers;
    
    public EventPublisherI() {
        this.subscribers = new ConcurrentHashMap<>();
        System.out.println("EventPublisherI servant created with zone filtering support");
    }
    
    @Override
    public String subscribe(EventSubscriberPrx subscriber, Current current) {
        String subscriptionId = UUID.randomUUID().toString();
        // Default: subscribe to all zones (empty set)
        subscribers.put(subscriptionId, new SubscriberInfo(subscriber, new HashSet<>()));
        
        System.out.println("New subscriber registered: " + subscriptionId);
        System.out.println("  Zones: ALL (no filter)");
        System.out.println("  Total subscribers: " + subscribers.size());
        
        return subscriptionId;
    }
    
    @Override
    public String subscribeWithZones(EventSubscriberPrx subscriber, String[] zones, Current current) {
        String subscriptionId = UUID.randomUUID().toString();
        Set<String> zoneSet = new HashSet<>();
        if (zones != null) {
            for (String zone : zones) {
                zoneSet.add(zone);
            }
        }
        subscribers.put(subscriptionId, new SubscriberInfo(subscriber, zoneSet));
        
        System.out.println("New subscriber registered with zone filter: " + subscriptionId);
        System.out.println("  Zones: " + (zoneSet.isEmpty() ? "ALL" : zoneSet));
        System.out.println("  Total subscribers: " + subscribers.size());
        
        return subscriptionId;
    }
    
    @Override
    public void unsubscribe(String subscriptionId, Current current) {
        SubscriberInfo removed = subscribers.remove(subscriptionId);
        
        if (removed != null) {
            System.out.println("Subscriber unregistered: " + subscriptionId);
            System.out.println("  Total subscribers: " + subscribers.size());
        } else {
            System.out.println("Subscription not found: " + subscriptionId);
        }
    }
    
    @Override
    public void publishEvent(Event event, Current current) {
        System.out.println("Publishing event: " + event.type + " - " + event.message);
        notifySubscribers(event);
    }
    
    /**
     * Publish an event to subscribers with zone filtering (called internally by Observer).
     * Only subscribers interested in the event's zone will receive it.
     */
    public void notifySubscribers(Event event) {
        // Extract zone from event message
        String eventZone = extractZoneFromMessage(event.message);
        
        int notifiedCount = 0;
        int filteredCount = 0;
        
        for (Map.Entry<String, SubscriberInfo> entry : subscribers.entrySet()) {
            String id = entry.getKey();
            SubscriberInfo info = entry.getValue();
            
            // Check if subscriber wants this zone
            if (!info.zones.isEmpty() && eventZone != null && !info.zones.contains(eventZone)) {
                filteredCount++;
                continue; // Skip this subscriber
            }
            
            try {
                info.proxy.onEvent(event);
                notifiedCount++;
            } catch (Exception e) {
                System.err.println("Failed to notify subscriber " + id + ": " + e.getMessage());
                subscribers.remove(id);
            }
        }
        
        if (filteredCount > 0) {
            System.out.println("  Notified: " + notifiedCount + " subscribers, Filtered: " + filteredCount);
        }
    }
    
    /**
     * Extract zone ID from event message.
     * Message format: "Bus 514069 in Zone Z29 (Arc ARC_Z29)"
     */
    private String extractZoneFromMessage(String message) {
        if (message == null) return null;
        
        try {
            int zoneIndex = message.indexOf("Zone ");
            if (zoneIndex != -1) {
                int startIndex = zoneIndex + 5;
                int endIndex = message.indexOf(" ", startIndex);
                if (endIndex == -1) endIndex = message.indexOf("(", startIndex);
                if (endIndex == -1) endIndex = message.length();
                
                return message.substring(startIndex, endIndex).trim();
            }
        } catch (Exception e) {
            System.err.println("[EventPublisherI] Error extracting zone: " + e.getMessage());
        }
        
        return null;
    }
    
    public int getSubscriberCount() {
        return subscribers.size();
    }
}
