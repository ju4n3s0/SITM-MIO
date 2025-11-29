package com.sitm.mio.operationcontrol.interfaces;

import com.sitm.mio.operationcontrol.model.OperatorCredentials;
import com.sitm.mio.operationcontrol.model.AuthenticatedOperatorData;
import com.sitm.mio.operationcontrol.model.ZoneStatisticsResponse;

/**
 * Interface for REST API client communicating with ProxyCache.
 * Defines the contract for POST and GET requests to DataCenter services.
 * 
 * Realized by: ProxyClient
 */
public interface IProxyClient {
    
    /**
     * Authenticate operator via POST request.
     * @param credentials Operator username and password
     * @return Authenticated operator data with token and assigned zones
     */
    AuthenticatedOperatorData authenticate(OperatorCredentials credentials);
    
    /**
     * Query zone statistics via GET request.
     * @param zoneId Zone identifier
     * @return Zone statistics including average speed and arc data
     */
    ZoneStatisticsResponse getZoneStatistics(String zoneId);
    
    /**
     * Logout operator via POST request.
     */
    void logout();
}
