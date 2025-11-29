package com.sitm.mio.operationcontrol.model;

import java.time.LocalDateTime;

/**
 * Event artifact representing a bus position update.
 * Received via WebSocket from ProxyCache/SubscriptionManager.
 * Artifact from deployment diagram: EventoPosicionBusActualizada
 */
public class BusPositionUpdatedEvent {
    
    private String busId;
    private Long arcId;
    private String zoneId;
    private Double speed;
    private Double latitude;
    private Double longitude;
    private Long timestamp;
    
    public BusPositionUpdatedEvent() {}
    
    public BusPositionUpdatedEvent(String busId, Long arcId, String zoneId, Double speed, 
                                   Double latitude, Double longitude, Long timestamp) {
        this.busId = busId;
        this.arcId = arcId;
        this.zoneId = zoneId;
        this.speed = speed;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }
    
    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }
    
    public Long getArcId() { return arcId; }
    public void setArcId(Long arcId) { this.arcId = arcId; }
    
    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }
    
    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
