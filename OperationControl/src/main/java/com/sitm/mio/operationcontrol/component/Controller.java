package com.sitm.mio.operationcontrol.component;

import com.sitm.mio.operationcontrol.interfaces.IController;
import com.sitm.mio.operationcontrol.model.*;
import java.util.List;

/**
 * Main business logic controller for the Operation Control System.
 * Component from deployment diagram: Controlador
 * 
 * Realizes: IController
 * 
 * Orchestrates:
 * - Authentication via ProxyClient (POST requests)
 * - Zone statistics queries via ProxyClient (GET requests)
 * - Real-time event subscription via EventReceiver (WebSocket)
 * - Event handling and business logic
 */
public class Controller implements IController {
    
    private final ProxyClient proxyClient;
    private final EventReceiver eventReceiver;
    private AuthenticatedOperatorData currentOperator;
    
    public Controller(ProxyClient proxyClient, EventReceiver eventReceiver) {
        this.proxyClient = proxyClient;
        this.eventReceiver = eventReceiver;
    }
    
    @Override
    public AuthenticatedOperatorData login(String username, String password) {
        // TODO: Implement login flow
        // 1. Call proxyClient.authenticate() (POST)
        // 2. Store currentOperator
        // 3. Connect to WebSocket with token
        // 4. Subscribe to assigned zones
        return null;
    }
    
    @Override
    public void logout() {
        // TODO: Implement logout flow
        // 1. Disconnect WebSocket
        // 2. Call proxyClient.logout() (POST)
        // 3. Clear currentOperator
    }
    
    @Override
    public ZoneStatisticsResponse queryZoneStatistics(String zoneId) {
        // TODO: Implement zone query (GET request)
        // 1. Validate operator has access to zone
        // 2. Call proxyClient.getZoneStatistics()
        return null;
    }
    
    @Override
    public List<String> getAssignedZones() {
        // TODO: Implement
        return null;
    }
    
    @Override
    public boolean isAuthenticated() {
        // TODO: Implement
        return false;
    }
    
    @Override
    public AuthenticatedOperatorData getCurrentOperator() {
        return currentOperator;
    }
}
