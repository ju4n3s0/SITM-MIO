package com.sitm.mio.operationcontrol.model;

import java.util.List;

/**
 * DTO for zone statistics response.
 * Response from GET /api/zone-statistics?zoneId={zoneId}.
 * Artifact from deployment diagram: RespuestaEstadisticasZona
 */
public class ZoneStatisticsResponse {
    
    private String zoneId;
    private Double avgSpeed;
    private Integer vehicleCount;
    private List<ZoneArcData> arcs;
    private String lastUpdate;
    
    // TODO: Implement getters, setters, and constructors
}
