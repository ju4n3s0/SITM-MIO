package com.sitm.mio.operationcontrol.component;

import com.sitm.mio.operationcontrol.interfaces.ITaskDelegator;
import com.sitm.mio.operationcontrol.interfaces.IResultReceiver;

/**
 * Worker strategy component for calculating average speeds.
 * Part of the Master-Slave calculation pattern.
 * Component from deployment diagram: CalcularVelocidadPromedio
 * 
 * Uses: ITaskDelegator (to interact with WorkerCalculator master)
 * Uses: IResultReceiver (to send results back)
 */
public class CalculateAverageSpeed {
    
    private final ITaskDelegator taskDelegator;
    private final IResultReceiver resultReceiver;
    
    public CalculateAverageSpeed(ITaskDelegator taskDelegator, IResultReceiver resultReceiver) {
        this.taskDelegator = taskDelegator;
        this.resultReceiver = resultReceiver;
    }
    
    /**
     * Calculate average speed for a zone.
     * @param zoneId Zone identifier
     * @return Average speed in km/h
     */
    public Double calculateZoneAverageSpeed(String zoneId) {
        // TODO: Implement zone average speed calculation
        // 1. Query recent speed data for zone
        // 2. Calculate weighted average
        // 3. Send result to resultReceiver
        return null;
    }
    
    /**
     * Calculate average speed for a specific arc.
     * @param arcId Arc identifier
     * @return Average speed in km/h
     */
    public Double calculateArcSpeed(Long arcId) {
        // TODO: Implement arc speed calculation
        return null;
    }
    
    /**
     * Calculate average speed for a time window.
     * @param zoneId Zone identifier
     * @param timeWindow Time window in minutes
     * @return Average speed
     */
    public Double calculateSpeedInTimeWindow(String zoneId, Integer timeWindow) {
        // TODO: Implement time-windowed speed calculation
        return null;
    }
}
