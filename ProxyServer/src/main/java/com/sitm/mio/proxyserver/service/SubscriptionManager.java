package com.sitm.mio.proxyserver.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages event subscriptions with zone-based filtering.
 * Subscribers can specify which zones they want to receive events from.
 */
public class SubscriptionManager {

    // Map of subscriber to their subscription criteria
    private final Map<EventSubscriber, SubscriptionCriteria> subscriptions = new ConcurrentHashMap<>();

    /**
     * Subscribe with specific filtering criteria.
     * @param subscriber The subscriber to register
     * @param criteria The filtering criteria (zone IDs)
     */
    public void subscribe(EventSubscriber subscriber, SubscriptionCriteria criteria) {
        if (subscriber != null) {
            subscriptions.put(subscriber, criteria != null ? criteria : SubscriptionCriteria.all());
            System.out.println("Subscriber registered with criteria: " + criteria);
        }
    }

    /**
     * Subscribe without filtering (receives all events).
     * @param subscriber The subscriber to register
     */
    public void subscribe(EventSubscriber subscriber) {
        subscribe(subscriber, SubscriptionCriteria.all());
    }

    /**
     * Unsubscribe a subscriber.
     * @param subscriber The subscriber to remove
     */
    public void unsubscribe(EventSubscriber subscriber) {
        subscriptions.remove(subscriber);
    }

    /**
     * Get all subscribers (without filtering).
     * @return Set of all subscribers
     */
    public Set<EventSubscriber> getSubscribers() {
        return subscriptions.keySet();
    }

    /**
     * Get subscribers that match the given event metadata.
     * This filters subscribers based on their subscription criteria.
     * 
     * @param eventMetadata Metadata about the event (zone, type, etc.)
     * @return Set of subscribers that should receive this event
     */
    public Set<EventSubscriber> getMatchingSubscribers(EventMetadata eventMetadata) {
        return subscriptions.entrySet().stream()
            .filter(entry -> entry.getValue().matches(eventMetadata))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    /**
     * Get the subscription criteria for a specific subscriber.
     * @param subscriber The subscriber
     * @return The subscription criteria, or null if not subscribed
     */
    public SubscriptionCriteria getCriteria(EventSubscriber subscriber) {
        return subscriptions.get(subscriber);
    }

    /**
     * Get the total number of subscribers.
     * @return Number of active subscriptions
     */
    public int getSubscriberCount() {
        return subscriptions.size();
    }

    /**
     * Represents filtering criteria for subscriptions.
     * Only filters by zone IDs.
     */
    public static class SubscriptionCriteria {
        private final Set<String> zoneIds;
        private final boolean subscribeToAll;

        private SubscriptionCriteria(Set<String> zoneIds, boolean subscribeToAll) {
            this.zoneIds = zoneIds != null ? new HashSet<>(zoneIds) : new HashSet<>();
            this.subscribeToAll = subscribeToAll;
        }

        /**
         * Create criteria that matches all events (all zones).
         */
        public static SubscriptionCriteria all() {
            return new SubscriptionCriteria(null, true);
        }

        /**
         * Create criteria for specific zones.
         * @param zoneIds Zone IDs to subscribe to
         */
        public static SubscriptionCriteria forZones(String... zoneIds) {
            return new SubscriptionCriteria(Set.of(zoneIds), false);
        }

        /**
         * Create criteria for specific zones.
         * @param zoneIds Set of zone IDs to subscribe to
         */
        public static SubscriptionCriteria forZones(Set<String> zoneIds) {
            return new SubscriptionCriteria(zoneIds, false);
        }

        /**
         * Check if this criteria matches the given event metadata.
         * Only checks zone matching.
         */
        public boolean matches(EventMetadata metadata) {
            if (subscribeToAll) {
                return true;
            }

            // Match if zone list is empty (match all) or contains the event's zone
            return zoneIds.isEmpty() || zoneIds.contains(metadata.getZoneId());
        }

        @Override
        public String toString() {
            if (subscribeToAll) {
                return "ALL_ZONES";
            }
            return "zones=" + zoneIds;
        }
    }

    /**
     * Metadata about an event for filtering purposes.
     */
    public static class EventMetadata {
        private final String zoneId;
        private final String eventType;
        private final long originId;
        private final long destinationId;

        public EventMetadata(String zoneId, String eventType, long originId, long destinationId) {
            this.zoneId = zoneId;
            this.eventType = eventType;
            this.originId = originId;
            this.destinationId = destinationId;
        }

        public String getZoneId() {
            return zoneId;
        }

        public String getEventType() {
            return eventType;
        }

        public long getOriginId() {
            return originId;
        }

        public long getDestinationId() {
            return destinationId;
        }

        @Override
        public String toString() {
            return "EventMetadata{zone='" + zoneId + "', type='" + eventType + 
                   "', origin=" + originId + ", dest=" + destinationId + "}";
        }
    }
}