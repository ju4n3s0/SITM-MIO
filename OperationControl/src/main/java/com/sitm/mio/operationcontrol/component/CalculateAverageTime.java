package com.sitm.mio.operationcontrol.component;

import com.sitm.mio.operationcontrol.interfaces.ITaskDelegator;
import com.sitm.mio.operationcontrol.interfaces.IResultReceiver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Worker strategy component for calculating average travel times.
 * Part of the Master-Slave calculation pattern.
 * Component from deployment diagram: CalcularTiempoPromedio
 * 
 * Uses: ITaskDelegator (to interact with WorkerCalculator master)
 * Uses: IResultReceiver (to send results back)
 */
public class CalculateAverageTime {
    
    private final ITaskDelegator taskDelegator;
    private final IResultReceiver resultReceiver;
    private final DataAccessLayer dataAccess;
    private final TravelTimeDataInserter dataInserter;
    
    public CalculateAverageTime(ITaskDelegator taskDelegator, IResultReceiver resultReceiver, 
                               DataAccessLayer dataAccess, TravelTimeDataInserter dataInserter) {
        this.taskDelegator = taskDelegator;
        this.resultReceiver = resultReceiver;
        this.dataAccess = dataAccess;
        this.dataInserter = dataInserter;
    }
    
    /**
     * Calculate average travel time for a route segment.
     * @param originStopId Origin stop ID
     * @param destinationStopId Destination stop ID
     * @param timeWindowMinutes Time window for historical data (default: 60 minutes)
     * @return Average time result with statistics
     */
    public Map<String, Object> calculateAverageTime(long originStopId, long destinationStopId, int timeWindowMinutes) {
        System.out.println("[CalculateAverageTime] Calculating for route: " + originStopId + " -> " + destinationStopId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("originStopId", originStopId);
        result.put("destinationStopId", destinationStopId);
        result.put("timeWindowMinutes", timeWindowMinutes);
        
        try {
            // Query historical travel time data via DataAccessLayer
            List<Double> travelTimes = dataAccess != null 
                ? dataAccess.queryTravelTimes(originStopId, destinationStopId, timeWindowMinutes)
                : simulateHistoricalData(originStopId, destinationStopId);
            
            if (travelTimes.isEmpty()) {
                result.put("status", "NO_DATA");
                result.put("averageTimeMinutes", null);
                System.out.println("[CalculateAverageTime] No historical data available");
                return result;
            }
            
            // Calculate statistics
            double sum = 0;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            
            for (double time : travelTimes) {
                sum += time;
                min = Math.min(min, time);
                max = Math.max(max, time);
            }
            
            double average = sum / travelTimes.size();
            
            // Calculate standard deviation
            double variance = 0;
            for (double time : travelTimes) {
                variance += Math.pow(time - average, 2);
            }
            double stdDev = Math.sqrt(variance / travelTimes.size());
            
            result.put("status", "SUCCESS");
            result.put("averageTimeMinutes", average);
            result.put("minTimeMinutes", min);
            result.put("maxTimeMinutes", max);
            result.put("standardDeviation", stdDev);
            result.put("sampleCount", travelTimes.size());
            result.put("confidence", calculateConfidence(travelTimes.size()));
            
            System.out.println(String.format("[CalculateAverageTime] Result: %.2f min (±%.2f) from %d samples",
                average, stdDev, travelTimes.size()));
            
            // Insert result into database for citizen queries
            if (dataInserter != null) {
                System.out.println("[CalculateAverageTime] Submitting result to database via ProxyServer...");
                // Use "UNKNOWN" zone for now - TODO: determine actual zone from stops
                dataInserter.insertFromCalculationResult("UNKNOWN", originStopId, destinationStopId, result);
            } else {
                System.out.println("[CalculateAverageTime] WARNING: dataInserter is null - cannot submit to database");
            }
            
            // Send result to receiver if available
            if (resultReceiver != null) {
                resultReceiver.receiveResult("AVERAGE_TIME", result);
            }
            
        } catch (Exception e) {
            System.err.println("[CalculateAverageTime] Error: " + e.getMessage());
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Calculate average time for a zone (all routes within zone).
     */
    public Map<String, Object> calculateZoneAverageTime(String zoneId, int timeWindowMinutes) {
        System.out.println("[CalculateAverageTime] Calculating for zone: " + zoneId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("zoneId", zoneId);
        result.put("timeWindowMinutes", timeWindowMinutes);
        
        // TODO: Query all routes in zone and calculate aggregate
        // For now, return mock data
        result.put("status", "SUCCESS");
        result.put("averageTimeMinutes", 15.5);
        result.put("routeCount", 10);
        
        return result;
    }
    
    /**
     * Simulate historical travel time data.
     * TODO: Replace with actual query to ProxyServer/DataCenter
     */
    private List<Double> simulateHistoricalData(long originStopId, long destinationStopId) {
        List<Double> times = new ArrayList<>();
        
        // Simulate 20 historical measurements with some variance
        double baseTime = 10.0 + (Math.abs(originStopId - destinationStopId) % 20);
        for (int i = 0; i < 20; i++) {
            double variance = (Math.random() - 0.5) * 4; // ±2 minutes variance
            times.add(baseTime + variance);
        }
        
        return times;
    }
    
    /**
     * Calculate confidence level based on sample size.
     */
    private String calculateConfidence(int sampleSize) {
        if (sampleSize >= 50) return "HIGH";
        if (sampleSize >= 20) return "MEDIUM";
        if (sampleSize >= 5) return "LOW";
        return "VERY_LOW";
    }
}
