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
    
    // Default constructor for Jackson
    public ZoneStatisticsUpdatedEvent() {
    }
    
    // Full constructor
    public ZoneStatisticsUpdatedEvent(String zoneId, Double avgSpeed, Integer vehicleCount, LocalDateTime timestamp) {
        this.zoneId = zoneId;
        this.avgSpeed = avgSpeed;
        this.vehicleCount = vehicleCount;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public String getZoneId() {
        return zoneId;
    }
    
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }
    
    public Double getAvgSpeed() {
        return avgSpeed;
    }
    
    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }
    
    public Integer getVehicleCount() {
        return vehicleCount;
    }
    
    public void setVehicleCount(Integer vehicleCount) {
        this.vehicleCount = vehicleCount;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "ZoneStatisticsUpdatedEvent{" +
                "zoneId='" + zoneId + '\'' +
                ", avgSpeed=" + avgSpeed +
                ", vehicleCount=" + vehicleCount +
                ", timestamp=" + timestamp +
                '}';
    }
}
