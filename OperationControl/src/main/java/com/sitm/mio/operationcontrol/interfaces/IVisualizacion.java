package com.sitm.mio.operationcontrol.interfaces;

import com.sitm.mio.operationcontrol.model.BusPositionUpdatedEvent;
import com.sitm.mio.operationcontrol.model.ZoneStatisticsResponse;
import java.util.List;

/**
 * Interface for UI visualization capabilities.
 * Defines the contract for displaying operational data to users.
 * 
 * Realized by: OperationControlUI
 * Used by: Controller
 */
public interface IVisualizacion {
    
    /**
     * Display bus position update on the UI.
     * @param event Bus position event
     */
    void displayBusPosition(BusPositionUpdatedEvent event);
    
    /**
     * Display zone statistics on the UI.
     * @param statistics Zone statistics data
     */
    void displayZoneStatistics(ZoneStatisticsResponse statistics);
    
    /**
     * Display assigned zones for the operator.
     * @param zones List of zone IDs
     */
    void displayAssignedZones(List<String> zones);
    
    /**
     * Display an alert message.
     * @param message Alert message
     */
    void displayAlert(String message);
    
    /**
     * Update connection status indicator.
     * @param connected Connection status
     */
    void updateConnectionStatus(boolean connected);
    
    /**
     * Display historical trends for a zone.
     * @param trends Trends data
     */
    void displayTrends(Object trends);
}
