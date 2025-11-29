package com.sitm.mio.operationcontrol.interfaces;

import com.sitm.mio.operationcontrol.model.AuthenticatedOperatorData;

/**
 * Interface for authentication information display.
 * Defines the contract for displaying authentication-related data to users.
 * 
 * Original name from deployment diagram: InformaciónDeAutenticación
 * 
 * Realized by: OperationControlUI
 * Used by: Controller
 */
public interface IAuthenticationInfo {
    
    /**
     * Display login prompt to the user.
     */
    void showLoginPrompt();
    
    /**
     * Display authenticated operator information.
     * @param operatorData Authenticated operator data
     */
    void displayAuthenticatedOperator(AuthenticatedOperatorData operatorData);
    
    /**
     * Display authentication error.
     * @param errorMessage Error message
     */
    void displayAuthenticationError(String errorMessage);
    
    /**
     * Clear authentication information (logout).
     */
    void clearAuthenticationInfo();
}
