package com.sitm.mio.datacenter.ice;

import com.zeroc.Ice.Current;
import SITM.Authenticator;
import SITM.OperatorAuthResult;
import SITM.OperatorCredentials;
import com.sitm.mio.datacenter.interfaces.IAuthenticator;

/**
 * ICE servant implementation for Authenticator interface.
 * Delegates to the DataCenter's Authenticator component.
 */
public class AuthenticatorI implements Authenticator {
    
    private final IAuthenticator authenticator;
    
    public AuthenticatorI(IAuthenticator authenticator) {
        this.authenticator = authenticator;
    }
    
    @Override
    public OperatorAuthResult authenticateOperator(OperatorCredentials credentials, Current current) {
        System.out.println("[AuthenticatorI] ICE call: authenticateOperator");
        System.out.println("  Username: " + credentials.username);
        System.out.println("  Client: " + current.con.toString());
        
        try {
            // Call the DataCenter's authenticator
            Object result = authenticator.authenticateOperator(
                credentials.username, 
                credentials.password
            );
            
            if (result == null) {
                System.out.println("[AuthenticatorI] Authentication failed");
                return null;
            }
            
            // Convert from DataCenter's OperatorAuthResult to ICE OperatorAuthResult
            com.sitm.mio.datacenter.model.OperatorAuthResult dcResult = 
                (com.sitm.mio.datacenter.model.OperatorAuthResult) result;
            
            OperatorAuthResult iceResult = new OperatorAuthResult();
            iceResult.operatorId = dcResult.getOperatorId();
            iceResult.username = dcResult.getUsername();
            iceResult.assignedZones = dcResult.getAssignedZones().toArray(new String[0]);
            iceResult.token = dcResult.getToken();
            
            System.out.println("[AuthenticatorI] Authentication successful");
            System.out.println("  Operator ID: " + iceResult.operatorId);
            System.out.println("  Zones: " + dcResult.getAssignedZones());
            System.out.println("  Token: " + iceResult.token);
            
            return iceResult;
            
        } catch (Exception e) {
            System.err.println("[AuthenticatorI] Error during authentication: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean validateToken(String token, Current current) {
        System.out.println("[AuthenticatorI] ICE call: validateToken");
        
        try {
            boolean valid = authenticator.validateToken(token);
            System.out.println("  Token valid: " + valid);
            return valid;
        } catch (Exception e) {
            System.err.println("[AuthenticatorI] Error validating token: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public void invalidateToken(String token, Current current) {
        System.out.println("[AuthenticatorI] ICE call: invalidateToken");
        
        try {
            // Find and remove session
            com.sitm.mio.datacenter.model.OperatorAuthResult session = 
                authenticator.findSessionByToken(token);
            
            if (session != null) {
                System.out.println("  Invalidated session for: " + session.getUsername());
                authenticator.removeSession(token);
            } else {
                System.out.println("  Token not found");
            }
        } catch (Exception e) {
            System.err.println("[AuthenticatorI] Error invalidating token: " + e.getMessage());
        }
    }
}
