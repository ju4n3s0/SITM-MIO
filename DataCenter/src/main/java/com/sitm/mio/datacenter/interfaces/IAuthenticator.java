package com.sitm.mio.datacenter.interfaces;

import com.sitm.mio.datacenter.model.OperatorAuthResult;

/**
 * Interface for authentication services.
 * Realized by: Autenticador
 */
public interface IAuthenticator {
    Object authenticateOperator(String username, String password);
    Object authenticateCitizen(String credentials);
    boolean validateToken(String token);
    OperatorAuthResult findSessionByToken(String token);
    void removeSession(String token);
}
