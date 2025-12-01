package com.sitm.mio.observer.ice;

import com.zeroc.Ice.Current;
import SITM.DataCenterEventSubscriber;
import SITM.EnrichedDatagram;

/**
 * ICE servant implementation for DataCenterEventSubscriber interface.
 * Observer subscribes to DataCenter's enriched datagram events.
 * 
 * Event chain:
 * DataCenter → Observer (this) → OperationControl
 */
public class DataCenterEventSubscriberI implements DataCenterEventSubscriber {
    
    private final EventPublisherI eventPublisher;
    
    public DataCenterEventSubscriberI(EventPublisherI eventPublisher) {
        this.eventPublisher = eventPublisher;
        System.out.println("DataCenterEventSubscriberI created - ready to receive from DataCenter");
    }
    
    @Override
    public void onEnrichedDatagram(EnrichedDatagram datagram, Current current) {
        System.out.println("========================================");
        System.out.println("ENRICHED DATAGRAM RECEIVED FROM DATACENTER");
        System.out.println("========================================");
        System.out.println("Datagram ID: " + datagram.datagramId);
        System.out.println("Bus ID: " + datagram.busId);
        System.out.println("Line ID: " + datagram.lineId);
        System.out.println("Zone: " + datagram.zoneId);
        System.out.println("Arc: " + datagram.arcId);
        System.out.println("Location: (" + datagram.latitude + ", " + datagram.longitude + ")");
        System.out.println("Timestamp: " + datagram.timestamp);
        System.out.println("========================================");
        System.out.println();
        
        // Convert to Event and forward to OperationControl subscribers
        SITM.Event event = new SITM.Event();
        event.type = SITM.EventType.RequestProcessed;
        event.source = "DataCenter";
        event.message = String.format("Bus %d in Zone %s (Arc %s)", 
            datagram.busId, datagram.zoneId, datagram.arcId);
        event.timestamp = datagram.timestamp;
        
        System.out.println("Forwarding to OperationControl subscribers...");
        eventPublisher.notifySubscribers(event);
    }
}
