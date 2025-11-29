package com.sitm.mio.operationcontrol.model;

import java.util.List;

/**
 * DTO for zone statistics response.
 * Response from GET /api/zone-statistics?zoneId={zoneId}.
 * Artifact from deployment diagram: RespuestaEstadisticasZona
 */
public class ZoneStatisticsResponse {
    
    private String zoneId;
    private Integer vehicleCount;
    private Double avgSpeed;
    private Long timestamp;
    
    public ZoneStatisticsResponse() {}
    
    public ZoneStatisticsResponse(String zoneId, Integer vehicleCount, Double avgSpeed, Long timestamp) {
        this.zoneId = zoneId;
        this.vehicleCount = vehicleCount;
        this.avgSpeed = avgSpeed;
        this.timestamp = timestamp;
    }
    
    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }
    
    public Integer getVehicleCount() { return vehicleCount; }
    public void setVehicleCount(Integer vehicleCount) { this.vehicleCount = vehicleCount; }
    
    public Double getAvgSpeed() { return avgSpeed; }
    public void setAvgSpeed(Double avgSpeed) { this.avgSpeed = avgSpeed; }
    
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
