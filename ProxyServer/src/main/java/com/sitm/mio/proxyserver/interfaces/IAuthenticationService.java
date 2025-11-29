package com.sitm.mio.proxyserver.interfaces;

/**
 * Interface for operator authentication and session management.
 * Handles login, logout, and session validation.
 * 
 * Realized by: AuthenticationService
 */
public interface IAuthenticationService {
    
    /**
     * Authenticate an operator with credentials.
     * 
     * @param username Operator username
     * @param password Operator password
     * @return Authentication token and operator data, or null if invalid
     */
    Object authenticate(String username, String password);
    
    /**
     * Validate an authentication token.
     * 
     * @param token Authentication token
     * @return true if valid, false otherwise
     */
    boolean validateToken(String token);
    
    /**
     * Logout an operator and invalidate their session.
     * 
     * @param token Authentication token
     * @return true if successful, false otherwise
     */
    boolean logout(String token);
    
    /**
     * Get operator data from token.
     * 
     * @param token Authentication token
     * @return Operator data including assigned zones
     */
    Object getOperatorData(String token);
}
