package com.sitm.mio.operationcontrol.model;

/**
 * DTO for operator authentication credentials.
 * Used for POST requests to authenticate operators.
 * Artifact from deployment diagram: CredencialesOperador
 */
public class OperatorCredentials {
    
    private String username;
    private String password;
    
    public OperatorCredentials() {}
    
    public OperatorCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
