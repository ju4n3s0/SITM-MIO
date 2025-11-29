package com.sitm.mio.observer.model;

import java.time.LocalDateTime;

/**
 * Event artifact representing zone statistics update.
 * Received via WebSocket from ProxyCache/SubscriptionManager.
 * Artifact from deployment diagram: EventoEstadisticasZonaActualizadas
 */
public class ZoneStatisticsUpdatedEvent {
    
    private String zoneId;
    private Double avgSpeed;
    private Integer vehicleCount;
    private LocalDateTime timestamp;
    
    // TODO: Implement getters, setters, and constructors
}
