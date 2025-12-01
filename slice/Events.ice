// Slice definitions for Event Streaming
// Defines interfaces for real-time event notifications to Observer

module SITM {
    
    // Event types
    enum EventType {
        CacheHit,
        CacheMiss,
        RequestProcessed,
        SystemAlert,
        ZoneUpdate
    };
    
    // Generic event structure
    struct Event {
        EventType type;
        string source;
        string message;
        long timestamp;
    };
    
    // Sequence of events
    sequence<Event> EventSeq;
    
    // Sequence of zone IDs for filtering
    sequence<string> ZoneSeq;
    
    // Event subscriber interface (implemented by OperationControl)
    interface EventSubscriber {
        /**
         * Receive an event notification.
         * @param event The event to process
         */
        void onEvent(Event event);
        
        /**
         * Receive multiple events in batch.
         * @param events Sequence of events
         */
        void onEvents(EventSeq events);
    };
    
    // Event publisher interface (implemented by Observer)
    interface EventPublisher {
        /**
         * Subscribe to all events (no filtering).
         * @param subscriber The subscriber proxy
         * @return Subscription ID
         */
        string subscribe(EventSubscriber* subscriber);
        
        /**
         * Subscribe to events from specific zones only.
         * @param subscriber The subscriber proxy
         * @param zones List of zone IDs to monitor (e.g., ["Z35", "Z29"])
         * @return Subscription ID
         */
        string subscribeWithZones(EventSubscriber* subscriber, ZoneSeq zones);
        
        /**
         * Unsubscribe from events.
         * @param subscriptionId The subscription ID to cancel
         */
        void unsubscribe(string subscriptionId);
        
        /**
         * Publish an event to all subscribers (with filtering).
         * @param event The event to publish
         */
        void publishEvent(Event event);
    };
};
