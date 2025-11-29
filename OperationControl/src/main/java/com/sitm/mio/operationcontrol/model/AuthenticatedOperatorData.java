package com.sitm.mio.operationcontrol.model;

import java.util.List;

/**
 * DTO for authenticated operator data including assigned zones.
 * Response from POST /api/auth/login.
 * Artifact from deployment diagram: DatosOperadorAutenticado
 */
public class AuthenticatedOperatorData {
    
    private String operatorId;
    private String username;
    private String fullName;
    private List<String> assignedZones;
    private String token;
    
    public AuthenticatedOperatorData() {}
    
    public AuthenticatedOperatorData(String operatorId, String username, String fullName,
                                     List<String> assignedZones, String token) {
        this.operatorId = operatorId;
        this.username = username;
        this.fullName = fullName;
        this.assignedZones = assignedZones;
        this.token = token;
    }
    
    public String getOperatorId() { return operatorId; }
    public void setOperatorId(String operatorId) { this.operatorId = operatorId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public List<String> getAssignedZones() { return assignedZones; }
    public void setAssignedZones(List<String> assignedZones) { this.assignedZones = assignedZones; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
