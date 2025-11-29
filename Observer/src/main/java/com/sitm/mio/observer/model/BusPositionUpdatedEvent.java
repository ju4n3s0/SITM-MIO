package com.sitm.mio.observer.model;

import java.time.LocalDateTime;

/**
 * Event artifact representing a bus position update.
 * Received via WebSocket from ProxyCache/SubscriptionManager.
 * Artifact from deployment diagram: EventoPosicionBusActualizada
 */
public class BusPositionUpdatedEvent {
    
    private Integer busId;
    private Long arcId;
    private String zoneId;
    private Double speed;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    
    // TODO: Implement getters, setters, and constructors
}
