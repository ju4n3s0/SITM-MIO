package com.sitm.mio.datacenter.interfaces;

/**
 * Interface for event distribution within DataCenter.
 * Central event bus for publishing and subscribing to domain events.
 * 
 * Realized by: EventBus
 */
public interface IEventBus {
    
    /**
     * Publish an event to all subscribers.
     * 
     * @param event Event object
     */
    void publish(Object event);
    
    /**
     * Subscribe to events of a specific type.
     * 
     * @param eventType Event class type
     * @param handler Event handler
     */
    void subscribe(Class<?> eventType, Object handler);
    
    /**
     * Unsubscribe from events.
     * 
     * @param eventType Event class type
     * @param handler Event handler
     */
    void unsubscribe(Class<?> eventType, Object handler);
    
    /**
     * Start the event bus.
     */
    void start();
    
    /**
     * Stop the event bus.
     */
    void stop();
}
