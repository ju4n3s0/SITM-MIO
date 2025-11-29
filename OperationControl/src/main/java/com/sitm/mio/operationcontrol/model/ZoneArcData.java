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
    
    // TODO: Implement getters, setters, and constructors
}
