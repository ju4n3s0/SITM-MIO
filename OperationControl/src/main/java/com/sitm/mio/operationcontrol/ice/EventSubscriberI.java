package com.sitm.mio.operationcontrol.ice;

import com.zeroc.Ice.Current;
import SITM.Event;
import SITM.EventSubscriber;

/**
 * ICE servant implementation for EventSubscriber interface.
 * OperationControl (Sistema Controlador de Operación) receives event notifications from Observer.
 * 
 * Implements the Observer pattern where:
 * - OperationControl is the Observer (EventSubscriber)
 * - Observer is the Subject (EventPublisher)
 */
public class EventSubscriberI implements EventSubscriber {
    
    public EventSubscriberI() {
        System.out.println("EventSubscriberI created - ready to receive notifications");
    }
    
    @Override
    public void onEvent(Event event, Current current) {
        System.out.println("========================================");
        System.out.println("EVENT RECEIVED FROM OBSERVER");
        System.out.println("========================================");
        System.out.println("Type: " + event.type);
        System.out.println("Source: " + event.source);
        System.out.println("Message: " + event.message);
        System.out.println("Timestamp: " + event.timestamp);
        System.out.println("========================================");
        System.out.println();
    }
    
    @Override
    public void onEvents(Event[] events, Current current) {
        System.out.println("========================================");
        System.out.println("BATCH OF " + events.length + " EVENTS RECEIVED");
        System.out.println("========================================");
        
        for (Event event : events) {
            System.out.println("- " + event.type + ": " + event.message);
        }
        
        System.out.println("========================================");
        System.out.println();
    }
}
