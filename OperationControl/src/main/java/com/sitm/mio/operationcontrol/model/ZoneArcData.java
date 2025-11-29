package com.sitm.mio.operationcontrol.model;

/**
 * DTO representing arc data within a zone.
 * Nested within ZoneStatisticsResponse.
 * Artifact from deployment diagram: DatosArcoZona
 */
public class ZoneArcData {
    
    private Long arcId;
    private Long fromStopId;
    private Long toStopId;
    private Double avgSpeed;
    private String timestamp;
    
    public ZoneArcData() {}
    
    public ZoneArcData(Long arcId, Long fromStopId, Long toStopId, Double avgSpeed, String timestamp) {
        this.arcId = arcId;
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.avgSpeed = avgSpeed;
        this.timestamp = timestamp;
    }
    
    public Long getArcId() { return arcId; }
    public void setArcId(Long arcId) { this.arcId = arcId; }
    
    public Long getFromStopId() { return fromStopId; }
    public void setFromStopId(Long fromStopId) { this.fromStopId = fromStopId; }
    
    public Long getToStopId() { return toStopId; }
    public void setToStopId(Long toStopId) { this.toStopId = toStopId; }
    
    public Double getAvgSpeed() { return avgSpeed; }
    public void setAvgSpeed(Double avgSpeed) { this.avgSpeed = avgSpeed; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
