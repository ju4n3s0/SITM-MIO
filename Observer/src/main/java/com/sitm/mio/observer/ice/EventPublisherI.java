package com.sitm.mio.observer.ice;

import com.zeroc.Ice.Current;
import SITM.Event;
import SITM.EventPublisher;
import SITM.EventSubscriberPrx;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ICE servant implementation for EventPublisher interface.
 * Observer (Sistema Observador) publishes events to subscribers.
 * 
 * Implements the Observer pattern where:
 * - Observer is the Subject (EventPublisher)
 * - OperationControl is the Observer (EventSubscriber)
 */
public class EventPublisherI implements EventPublisher {
    
    private final Map<String, EventSubscriberPrx> subscribers;
    
    public EventPublisherI() {
        this.subscribers = new ConcurrentHashMap<>();
        System.out.println("EventPublisherI servant created");
    }
    
    @Override
    public String subscribe(EventSubscriberPrx subscriber, Current current) {
        String subscriptionId = UUID.randomUUID().toString();
        subscribers.put(subscriptionId, subscriber);
        
        System.out.println("New subscriber registered: " + subscriptionId);
        System.out.println("  Total subscribers: " + subscribers.size());
        
        return subscriptionId;
    }
    
    @Override
    public void unsubscribe(String subscriptionId, Current current) {
        EventSubscriberPrx removed = subscribers.remove(subscriptionId);
        
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
        System.out.println("  Notifying " + subscribers.size() + " subscribers");
        
        // Notify all subscribers
        subscribers.forEach((id, subscriber) -> {
            try {
                subscriber.onEvent(event);
            } catch (Exception e) {
                System.err.println("Failed to notify subscriber " + id + ": " + e.getMessage());
                // Remove dead subscribers
                subscribers.remove(id);
            }
        });
    }
    
    /**
     * Publish an event to all subscribers (called internally by Observer).
     * This is the method Observer calls when new data arrives from ProxyServer.
     */
    public void notifySubscribers(Event event) {
        System.out.println("Observer notifying subscribers: " + event.type);
        
        subscribers.forEach((id, subscriber) -> {
            try {
                subscriber.onEvent(event);
            } catch (Exception e) {
                System.err.println("Failed to notify subscriber " + id + ": " + e.getMessage());
                subscribers.remove(id);
            }
        });
    }
    
    public int getSubscriberCount() {
        return subscribers.size();
    }
}
