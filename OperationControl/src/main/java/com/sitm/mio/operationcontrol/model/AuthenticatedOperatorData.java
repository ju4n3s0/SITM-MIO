package com.sitm.mio.operationcontrol.model;

import java.util.List;

/**
 * DTO for authenticated operator data including assigned zones.
 * Response from POST /api/auth/login.
 * Artifact from deployment diagram: DatosOperadorAutenticado
 */
public class AuthenticatedOperatorData {
    
    private Long operatorId;
    private String username;
    private String token;
    private List<String> assignedZones;
    private String role;
    
    // TODO: Implement getters, setters, and constructors
}
