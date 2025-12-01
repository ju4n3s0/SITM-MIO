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
    
    // Default constructor for Jackson
    public BusPositionUpdatedEvent() {
    }
    
    // Full constructor
    public BusPositionUpdatedEvent(Integer busId, Long arcId, String zoneId, Double speed,
                                   Double latitude, Double longitude, LocalDateTime timestamp) {
        this.busId = busId;
        this.arcId = arcId;
        this.zoneId = zoneId;
        this.speed = speed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public Integer getBusId() {
        return busId;
    }
    
    public void setBusId(Integer busId) {
        this.busId = busId;
    }
    
    public Long getArcId() {
        return arcId;
    }
    
    public void setArcId(Long arcId) {
        this.arcId = arcId;
    }
    
    public String getZoneId() {
        return zoneId;
    }
    
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }
    
    public Double getSpeed() {
        return speed;
    }
    
    public void setSpeed(Double speed) {
        this.speed = speed;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "BusPositionUpdatedEvent{" +
                "busId=" + busId +
                ", arcId=" + arcId +
                ", zoneId='" + zoneId + '\'' +
                ", speed=" + speed +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                '}';
    }
}
