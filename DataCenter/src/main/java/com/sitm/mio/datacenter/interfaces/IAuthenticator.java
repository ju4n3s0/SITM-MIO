package com.sitm.mio.datacenter.interfaces;

/**
 * Interface for authentication services.
 * Realized by: Autenticador
 */
public interface IAuthenticator {
    Object authenticateOperator(String username, String password);
    Object authenticateCitizen(String credentials);
    boolean validateToken(String token);
}
