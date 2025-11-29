package com.sitm.mio.operationcontrol.component;

import com.sitm.mio.operationcontrol.interfaces.IProxyClient;
import com.sitm.mio.operationcontrol.model.*;

/**
 * REST API client for communicating with ProxyCache.
 * Performs both GET and POST requests to access DataCenter services.
 * Component from deployment diagram: ClienteProxy
 * 
 * Realizes: IProxyClient
 * 
 * API Endpoints:
 * - POST /api/auth/login - Authenticate operator
 * - POST /api/auth/logout - Logout operator
 * - GET /api/zone-statistics?zoneId={id} - Query zone statistics
 */
public class ProxyClient implements IProxyClient {
    
    private final String baseUrl;
    private String authToken;
    
    public ProxyClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    /**
     * Authenticate operator via POST request.
     * @param credentials Operator username and password
     * @return Authenticated operator data with token and assigned zones
     */
    @Override
    public AuthenticatedOperatorData authenticate(OperatorCredentials credentials) {
        // TODO: Implement POST /api/auth/login
        return null;
    }
    
    /**
     * Query zone statistics via GET request.
     * @param zoneId Zone identifier
     * @return Zone statistics including average speed and arc data
     */
    @Override
    public ZoneStatisticsResponse getZoneStatistics(String zoneId) {
        // TODO: Implement GET /api/zone-statistics?zoneId={zoneId}
        return null;
    }
    
    /**
     * Logout operator via POST request.
     */
    @Override
    public void logout() {
        // TODO: Implement POST /api/auth/logout
    }
}
