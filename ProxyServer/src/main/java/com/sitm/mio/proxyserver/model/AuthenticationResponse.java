package com.sitm.mio.proxyserver.model;

import java.util.List;

/**
 * DTO for authentication response.
 */
public class AuthenticationResponse {
    private String token;
    private String operatorId;
    private String username;
    private List<String> assignedZones;
    
    public AuthenticationResponse() {}
    
    public AuthenticationResponse(String token, String operatorId, String username, List<String> assignedZones) {
        this.token = token;
        this.operatorId = operatorId;
        this.username = username;
        this.assignedZones = assignedZones;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getOperatorId() {
        return operatorId;
    }
    
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public List<String> getAssignedZones() {
        return assignedZones;
    }
    
    public void setAssignedZones(List<String> assignedZones) {
        this.assignedZones = assignedZones;
    }
}
