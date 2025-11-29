package com.sitm.mio.operationcontrol.interfaces;

import com.sitm.mio.operationcontrol.model.AuthenticatedOperatorData;
import com.sitm.mio.operationcontrol.model.ZoneStatisticsResponse;
import java.util.List;

/**
 * Interface for main business logic controller.
 * Orchestrates authentication, event reception, and data queries.
 * 
 * Realized by: Controller
 */
public interface IController {
    
    /**
     * Authenticate operator and initialize event subscriptions.
     * @param username Operator username
     * @param password Operator password
     * @return Authenticated operator data
     */
    AuthenticatedOperatorData login(String username, String password);
    
    /**
     * Logout operator and disconnect from event stream.
     */
    void logout();
    
    /**
     * Query zone statistics from DataCenter via ProxyCache.
     * @param zoneId Zone identifier
     * @return Zone statistics
     */
    ZoneStatisticsResponse queryZoneStatistics(String zoneId);
    
    /**
     * Get list of assigned zones for current operator.
     * @return List of zone IDs
     */
    List<String> getAssignedZones();
    
    /**
     * Check if operator is authenticated.
     * @return true if authenticated, false otherwise
     */
    boolean isAuthenticated();
    
    /**
     * Get current operator data.
     * @return Authenticated operator data or null if not authenticated
     */
    AuthenticatedOperatorData getCurrentOperator();
}
