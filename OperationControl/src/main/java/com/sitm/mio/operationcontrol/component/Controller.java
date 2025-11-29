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
 * - IProxyClient - REST API communication with ProxyCache (authentication, zone queries)
 * - IEventReceiver - WebSocket event streaming from Observer (real-time events)
 * - IVisualizacion - UI visualization updates
 * - ITaskDelegator - Calculation task delegation (Master-Slave pattern)
 * - IAlertSender - Alert notifications (optional)
 * - IReportSender - Report generation (optional)
 * - IAnalyticsClient - Analytics queries from Observer module (REST API, port 8081)
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
    private final IAnalyticsClient analyticsClient;
    
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
        IReportSender reportSender,
        IAnalyticsClient analyticsClient
    ) {
        this.proxyClient = proxyClient;
        this.eventReceiver = eventReceiver;
        this.visualization = visualization;
        this.taskDelegator = taskDelegator;
        this.alertSender = alertSender;
        this.reportSender = reportSender;
        this.analyticsClient = analyticsClient;
    }
    
    @Override
    public AuthenticatedOperatorData login(String username, String password) {
        OperatorCredentials credentials = new OperatorCredentials(username, password);
        
        currentOperator = proxyClient.authenticate(credentials);
        
        visualization.displayAssignedZones(currentOperator.getAssignedZones());
        
        eventReceiver.connect(currentOperator.getToken());
        eventReceiver.subscribeToZones(currentOperator.getAssignedZones());
        
        visualization.updateConnectionStatus(true);
        
        return currentOperator;
    }
    
    @Override
    public void logout() {
        eventReceiver.disconnect();
        proxyClient.logout();
        currentOperator = null;
        
        visualization.updateConnectionStatus(false);
    }
    
    @Override
    public ZoneStatisticsResponse queryZoneStatistics(String zoneId) {
        if (!currentOperator.getAssignedZones().contains(zoneId)) {
            visualization.displayAlert("No access to zone: " + zoneId);
            return null;
        }
        
        ZoneStatisticsResponse stats = proxyClient.getZoneStatistics(zoneId);
        visualization.displayZoneStatistics(stats);
        
        return stats;
    }
    
    @Override
    public List<String> getAssignedZones() {
        if (currentOperator != null) {
            return currentOperator.getAssignedZones();
        }
        return null;
    }
    
    @Override
    public boolean isAuthenticated() {
        return currentOperator != null && eventReceiver.isConnected();
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
        visualization.displayBusPosition(event);
        
        if (event.getSpeed() < 10.0) {
            alertSender.sendBusAlert(event.getBusId(), "LOW_SPEED", 
            "Bus " + event.getBusId() + " moving slowly");
        }
        
        if (taskDelegator.hasAvailableWorkers()) {
            taskDelegator.delegateTask("UPDATE_ZONE_STATS", event.getZoneId());
        }
    }
    
    /**
     * Request a report for a zone.
     * Demonstrates usage of IReportSender interface.
     * 
     * @param zoneId Zone identifier
     * @param timeRange Time range for report
     */
    public void requestZoneReport(String zoneId, String timeRange) {
        if (!getAssignedZones().contains(zoneId)) {
            visualization.displayAlert("No access to zone: " + zoneId);
            return;
        }
        
        String reportId = reportSender.sendZonePerformanceReport(zoneId, timeRange);
        visualization.displayAlert("Report " + reportId + " requested");
    }
    
    /**
     * View system-wide analytics from Observer module.
     * Demonstrates usage of IAnalyticsClient interface for inter-module communication.
     * 
     * This allows operators to see system-wide analytics in addition to
     * their zone-specific data.
     */
    public void viewSystemAnalytics() {
        if (!analyticsClient.isObserverAvailable()) {
            visualization.displayAlert("Observer system unavailable");
            return;
        }
        
        Object systemAnalytics = analyticsClient.getSystemAnalytics();
        visualization.displayAlert("System Analytics: " + systemAnalytics);
    }
    
    /**
     * View historical trends for a zone from Observer module.
     * Demonstrates cross-module analytics integration.
     * 
     * @param zoneId Zone identifier
     * @param timeRange Time range (e.g., "1h", "24h", "7d")
     */
    public void viewZoneTrends(String zoneId, String timeRange) {
        if (!getAssignedZones().contains(zoneId)) {
            visualization.displayAlert("No access to zone: " + zoneId);
            return;
        }
        
        Object trends = analyticsClient.getHistoricalTrends(timeRange);
        visualization.displayTrends(trends);
    }
}
