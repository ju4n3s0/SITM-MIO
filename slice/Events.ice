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
    
    // Event subscriber interface (implemented by Observer)
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
    
    // Event publisher interface (implemented by ProxyServer)
    interface EventPublisher {
        /**
         * Subscribe to events.
         * @param subscriber The subscriber proxy
         * @return Subscription ID
         */
        string subscribe(EventSubscriber* subscriber);
        
        /**
         * Unsubscribe from events.
         * @param subscriptionId The subscription ID to cancel
         */
        void unsubscribe(string subscriptionId);
        
        /**
         * Publish an event to all subscribers.
         * @param event The event to publish
         */
        void publishEvent(Event event);
    };
};
