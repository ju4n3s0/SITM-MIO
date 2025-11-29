package com.sitm.mio.operationcontrol.component;

import com.sitm.mio.operationcontrol.interfaces.*;
import com.sitm.mio.operationcontrol.model.*;
import java.util.List;

/**
 * Main business logic controller for the Operation Control System.
 * Component from deployment diagram: Controlador
 * 
 * Realizes: IController
 * 
 * Uses (depends on interfaces):
 * - IProxyClient - REST API communication
 * - IEventReceiver - WebSocket event reception
 * - IVisualizacion - UI visualization updates
 * - ITaskDelegator - Calculation task delegation
 * - IAlertSender - Alert notifications (optional)
 * - IReportSender - Report generation (optional)
 * 
 * Follows Dependency Inversion Principle:
 * Controller depends on abstractions (interfaces), not concrete implementations
 */
public class Controller implements IController {
    
    // Core dependencies (interfaces)
    private final IProxyClient proxyClient;
    private final IEventReceiver eventReceiver;
    private final IVisualizacion visualization;
    private final ITaskDelegator taskDelegator;
    
    // Optional dependencies (interfaces)
    private final IAlertSender alertSender;
    private final IReportSender reportSender;
    
    // State
    private AuthenticatedOperatorData currentOperator;
    
    /**
     * Constructor with all dependencies injected as interfaces.
     * Follows Dependency Inversion Principle.
     */
    public Controller(
        IProxyClient proxyClient,
        IEventReceiver eventReceiver,
        IVisualizacion visualization,
        ITaskDelegator taskDelegator,
        IAlertSender alertSender,
        IReportSender reportSender
    ) {
        this.proxyClient = proxyClient;
        this.eventReceiver = eventReceiver;
        this.visualization = visualization;
        this.taskDelegator = taskDelegator;
        this.alertSender = alertSender;
        this.reportSender = reportSender;
    }
    
    @Override
    public AuthenticatedOperatorData login(String username, String password) {
        // TODO: Implement login flow
        // 1. Create credentials
        // OperatorCredentials credentials = new OperatorCredentials(username, password);
        // 
        // 2. Authenticate via IProxyClient (POST)
        // currentOperator = proxyClient.authenticate(credentials);
        // 
        // 3. Display authenticated operator via IVisualizacion
        // visualization.displayAuthenticatedOperator(currentOperator);
        // visualization.displayAssignedZones(currentOperator.getAssignedZones());
        // 
        // 4. Connect to WebSocket via IEventReceiver
        // eventReceiver.connect(currentOperator.getToken());
        // eventReceiver.subscribeToZones(currentOperator.getAssignedZones());
        // 
        // 5. Update UI connection status
        // visualization.updateConnectionStatus(true);
        
        return null;
    }
    
    @Override
    public void logout() {
        // TODO: Implement logout flow
        // 1. Disconnect WebSocket via IEventReceiver
        // eventReceiver.disconnect();
        // 
        // 2. Logout via IProxyClient (POST)
        // proxyClient.logout();
        // 
        // 3. Clear state
        // currentOperator = null;
        // 
        // 4. Update UI via IVisualizacion
        // visualization.updateConnectionStatus(false);
    }
    
    @Override
    public ZoneStatisticsResponse queryZoneStatistics(String zoneId) {
        // TODO: Implement zone query
        // 1. Validate operator has access to zone
        // if (!currentOperator.getAssignedZones().contains(zoneId)) {
        //     visualization.displayAlert("No access to zone: " + zoneId);
        //     return null;
        // }
        // 
        // 2. Query via IProxyClient (GET)
        // ZoneStatisticsResponse stats = proxyClient.getZoneStatistics(zoneId);
        // 
        // 3. Display via IVisualizacion
        // visualization.displayZoneStatistics(stats);
        // 
        // return stats;
        
        return null;
    }
    
    @Override
    public List<String> getAssignedZones() {
        // TODO: Implement
        // if (currentOperator != null) {
        //     return currentOperator.getAssignedZones();
        // }
        return null;
    }
    
    @Override
    public boolean isAuthenticated() {
        // TODO: Implement
        // return currentOperator != null && eventReceiver.isConnected();
        return false;
    }
    
    @Override
    public AuthenticatedOperatorData getCurrentOperator() {
        return currentOperator;
    }
    
    /**
     * Handle incoming bus position event.
     * Demonstrates usage of multiple interfaces:
     * - IVisualizacion (display event)
     * - ITaskDelegator (delegate calculations)
     * - IAlertSender (send alerts if needed)
     * 
     * @param event Bus position event
     */
    public void handleBusPositionEvent(BusPositionUpdatedEvent event) {
        // TODO: Implement event handling
        // 1. Display event via IVisualizacion
        // visualization.displayBusPosition(event);
        // 
        // 2. If speed is too low, send alert via IAlertSender
        // if (event.getSpeed() < 10.0) {
        //     alertSender.sendBusAlert(event.getBusId(), "LOW_SPEED", 
        //         "Bus " + event.getBusId() + " moving slowly");
        // }
        // 
        // 3. Delegate zone statistics update via ITaskDelegator
        // if (taskDelegator.hasAvailableWorkers()) {
        //     taskDelegator.delegateTask("UPDATE_ZONE_STATS", event.getZoneId());
        // }
    }
    
    /**
     * Request a report for a zone.
     * Demonstrates usage of IReportSender interface.
     * 
     * @param zoneId Zone identifier
     * @param timeRange Time range for report
     */
    public void requestZoneReport(String zoneId, String timeRange) {
        // TODO: Implement report request
        // 1. Validate zone access
        // if (!getAssignedZones().contains(zoneId)) {
        //     visualization.displayAlert("No access to zone: " + zoneId);
        //     return;
        // }
        // 
        // 2. Request report via IReportSender
        // String reportId = reportSender.sendZonePerformanceReport(zoneId, timeRange);
        // 
        // 3. Notify user via IVisualizacion
        // visualization.displayAlert("Report " + reportId + " requested");
    }
}
