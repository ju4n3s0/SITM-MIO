package com.sitm.mio.observer.interfaces;

import com.sitm.mio.observer.model.BusPositionUpdatedEvent;
import com.sitm.mio.observer.model.ZoneStatisticsUpdatedEvent;

/**
 * Interface for real-time analytics processing.
 * Defines the contract for processing bus and zone events.
 * 
 * Realized by: AnalyticsUpdater
 */
public interface IAnalyticsProcessor {
    
    /**
     * Start processing analytics events.
     */
    void start();
    
    /**
     * Handle bus position update event.
     * @param event Bus position event
     */
    void handleBusPositionUpdate(BusPositionUpdatedEvent event);
    
    /**
     * Handle zone statistics update event.
     * @param event Zone statistics event
     */
    void handleZoneStatisticsUpdate(ZoneStatisticsUpdatedEvent event);
    
    /**
     * Stop processing analytics events.
     */
    void stop();
}
