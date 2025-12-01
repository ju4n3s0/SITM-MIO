package com.sitm.mio.observer.ice;

import com.zeroc.Ice.Current;
import SITM.Event;
import SITM.EventSubscriber;

/**
 * ICE servant implementation for EventSubscriber interface.
 * Observer subscribes to ProxyServer's events.
 * 
 * Event chain:
 * DataCenter → ProxyServer → Observer (this) → OperationControl
 */
public class ProxyServerEventSubscriberI implements EventSubscriber {
    
    private final EventPublisherI eventPublisher;
    
    public ProxyServerEventSubscriberI(EventPublisherI eventPublisher) {
        this.eventPublisher = eventPublisher;
        System.out.println("ProxyServerEventSubscriberI created - ready to receive from ProxyServer");
    }
    
    @Override
    public void onEvent(Event event, Current current) {
        System.out.println("========================================");
        System.out.println("EVENT RECEIVED FROM PROXYSERVER");
        System.out.println("========================================");
        System.out.println("Type: " + event.type);
        System.out.println("Source: " + event.source);
        System.out.println("Message: " + event.message);
        System.out.println("Timestamp: " + event.timestamp);
        System.out.println("========================================");
        System.out.println();
        
        // Forward event to OperationControl subscribers
        System.out.println("Forwarding event to OperationControl subscribers...");
        eventPublisher.notifySubscribers(event);
    }
    
    @Override
    public void onEvents(Event[] events, Current current) {
        System.out.println("========================================");
        System.out.println("BATCH OF " + events.length + " EVENTS FROM PROXYSERVER");
        System.out.println("========================================");
        
        for (Event event : events) {
            System.out.println("- " + event.type + ": " + event.message);
            // Forward each event
            eventPublisher.notifySubscribers(event);
        }
        
        System.out.println("========================================");
        System.out.println();
    }
}
