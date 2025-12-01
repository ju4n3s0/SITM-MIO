package com.sitm.mio.datacenter.ice;

import com.zeroc.Ice.Current;
import SITM.DataCenterEventSubscriber;
import SITM.DataCenterEventSubscriberPrx;
import SITM.EnrichedDatagram;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * ICE Servant for publishing enriched datagrams to ProxyServer.
 * Manages subscriptions and broadcasts events.
 */
public class DataCenterEventPublisherI implements SITM.DataCenterEventPublisher {
    
    private final Set<DataCenterEventSubscriberPrx> subscribers = new CopyOnWriteArraySet<>();
    
    @Override
    public void subscribe(DataCenterEventSubscriberPrx subscriber, Current current) {
        if (subscriber != null) {
            subscribers.add(subscriber);
            System.out.println("[DataCenterEventPublisher] New subscriber registered. Total: " + subscribers.size());
        }
    }
    
    @Override
    public void unsubscribe(DataCenterEventSubscriberPrx subscriber, Current current) {
        subscribers.remove(subscriber);
        System.out.println("[DataCenterEventPublisher] Subscriber removed. Total: " + subscribers.size());
    }
    
    /**
     * Publish an enriched datagram to all subscribers.
     * Called internally by DataCenter when new data is processed.
     * 
     * @param datagram The enriched datagram to publish
     */
    public void publishEnrichedDatagram(EnrichedDatagram datagram) {
        if (subscribers.isEmpty()) {
            return;
        }
        
        System.out.println("[DataCenterEventPublisher] Publishing datagram " + datagram.datagramId + 
                          " to " + subscribers.size() + " subscriber(s)");
        
        // Broadcast to all subscribers
        for (DataCenterEventSubscriberPrx subscriber : subscribers) {
            try {
                subscriber.onEnrichedDatagram(datagram);
            } catch (Exception e) {
                System.err.println("[DataCenterEventPublisher] Error notifying subscriber: " + e.getMessage());
                // Remove dead subscriber
                subscribers.remove(subscriber);
            }
        }
    }
    
    /**
     * Get the number of active subscribers.
     */
    public int getSubscriberCount() {
        return subscribers.size();
    }
}
