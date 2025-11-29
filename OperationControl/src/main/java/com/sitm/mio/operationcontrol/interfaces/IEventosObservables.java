package com.sitm.mio.operationcontrol.interfaces;

/**
 * Interface for observable event streams.
 * Defines the contract for components that provide event observation capabilities.
 * 
 * Realized by: EventReceiver
 * Used by: Event consumers that need to observe real-time events
 */
public interface IEventosObservables {
    
    /**
     * Register an observer for events.
     * @param observer Observer callback
     */
    void registerObserver(Object observer);
    
    /**
     * Unregister an observer.
     * @param observer Observer to remove
     */
    void unregisterObserver(Object observer);
    
    /**
     * Notify all registered observers of an event.
     * @param event Event data
     */
    void notifyObservers(Object event);
}
