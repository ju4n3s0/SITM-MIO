package com.sitm.mio.proxyserver.interfaces;

/**
 * Interface for event stream routing.
 * Routes events from EventBus to appropriate WebSocket subscriptions.
 * 
 * Realized by: EventStreamRouter
 */
public interface IEventStreamRouter {
    
    /**
     * Route an event to subscribers based on zone.
     * 
     * @param event Event object
     * @param zoneId Zone identifier
     */
    void routeToZone(Object event, String zoneId);
    
    /**
     * Route event to all subscribers (system-wide).
     * 
     * @param event Event object
     */
    void routeToAll(Object event);
    
    /**
     * Route event to specific subscription type.
     * 
     * @param event Event object
     * @param subscriptionType Type ("operator" or "observer")
     */
    void routeToSubscriptionType(Object event, String subscriptionType);
    
    /**
     * Register a routing rule.
     * 
     * @param eventType Event type
     * @param routingStrategy Routing strategy
     */
    void registerRoutingRule(String eventType, String routingStrategy);
    
    /**
     * Start routing engine.
     */
    void start();
    
    /**
     * Stop routing engine.
     */
    void stop();
}
