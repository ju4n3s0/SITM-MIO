package com.sitm.mio.proxyserver.service;

import java.util.Objects;
import java.util.Set;

/**
 * Routes events to subscribers based on their subscription criteria.
 * Filters which subscribers receive which events based on metadata.
 */
public class EventStreamRouter {

    private final SubscriptionManager subscriptionManager;

    public EventStreamRouter(SubscriptionManager subscriptionManager) {
        this.subscriptionManager = Objects.requireNonNull(subscriptionManager);
    }

    /**
     * Publish event to all subscribers (no filtering).
     * Use this for broadcast events that all subscribers should receive.
     * @param event The event to publish
     */
    public void publishEvent(Object event) {
        for (EventSubscriber subscriber : subscriptionManager.getSubscribers()) {
            try {
                subscriber.handleEvent(event);
            } catch (Exception e) {
                System.err.println("Error notifying subscriber: " + e.getMessage());
            }
        }
    }

    /**
     * Publish event with filtering based on metadata.
     * Only subscribers matching the criteria will receive the event.
     * 
     * @param event The event to publish
     * @param metadata Event metadata for filtering (zone, type, etc.)
     */
    public void publishEvent(Object event, SubscriptionManager.EventMetadata metadata) {
        Set<EventSubscriber> matchingSubscribers = subscriptionManager.getMatchingSubscribers(metadata);
        
        System.out.println("Publishing event with metadata: " + metadata);
        System.out.println("  Matching subscribers: " + matchingSubscribers.size() + "/" + subscriptionManager.getSubscriberCount());
        
        for (EventSubscriber subscriber : matchingSubscribers) {
            try {
                subscriber.handleEvent(event);
            } catch (Exception e) {
                System.err.println("Error notifying subscriber: " + e.getMessage());
            }
        }
    }
}