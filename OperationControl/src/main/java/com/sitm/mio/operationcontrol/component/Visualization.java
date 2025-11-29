package com.sitm.mio.operationcontrol.component;

import com.sitm.mio.operationcontrol.interfaces.IVisualizacion;
import com.sitm.mio.operationcontrol.model.BusPositionUpdatedEvent;
import com.sitm.mio.operationcontrol.model.ZoneStatisticsResponse;
import java.util.List;

/**
 * Visualization component for displaying operational data.
 * Implements IVisualizacion interface.
 * 
 * Realizes: IVisualizacion
 * Used by: Controller
 */
public class Visualization implements IVisualizacion {
    
    @Override
    public void displayBusPosition(BusPositionUpdatedEvent event) {
        // TODO: Implement bus position display
        System.out.println("Bus Position: " + event.getBusId() + 
                         " at Zone " + event.getZoneId() + 
                         " Speed: " + event.getSpeed());
    }
    
    @Override
    public void displayZoneStatistics(ZoneStatisticsResponse statistics) {
        // TODO: Implement zone statistics display
        System.out.println("Zone Statistics: " + statistics.getZoneId() +
                         " Vehicles: " + statistics.getVehicleCount() +
                         " Avg Speed: " + statistics.getAvgSpeed());
    }
    
    @Override
    public void displayAssignedZones(List<String> zones) {
        // TODO: Implement assigned zones display
        System.out.println("Assigned Zones: " + String.join(", ", zones));
    }
    
    @Override
    public void displayAlert(String message) {
        // TODO: Implement alert display
        System.out.println("ALERT: " + message);
    }
    
    @Override
    public void updateConnectionStatus(boolean connected) {
        // TODO: Implement connection status update
        System.out.println("Connection Status: " + (connected ? "Connected" : "Disconnected"));
    }
    
    @Override
    public void displayTrends(Object trends) {
        // TODO: Implement trends display
        System.out.println("Displaying trends: " + trends);
    }
}
