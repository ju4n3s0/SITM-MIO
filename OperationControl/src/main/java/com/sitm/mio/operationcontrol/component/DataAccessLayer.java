package com.sitm.mio.operationcontrol.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Layer for OperationControl.
 * Queries data from Observer via ICE (NO DIRECT DATABASE ACCESS).
 * 
 * Data Sources:
 * - Observer analytics via ICE (primary)
 * - Simulation (fallback when Observer unavailable)
 * 
 * NOTE: This does NOT query from datagram_history!
 * - datagram_history = raw real-time data source
 * - travel_time_stats = calculated statistics (what we INSERT to)
 * 
 * Supports:
 * 1. Travel time queries - for calculating new statistics
 * 2. Arc speed queries - for operation controllers
 */
public class DataAccessLayer {
    
    private final ObserverAnalyticsClient observerClient;
    
    public DataAccessLayer(ObserverAnalyticsClient observerClient) {
        this.observerClient = observerClient;
    }
    
    /**
     * Query historical travel times between two stops.
     * @param originStopId Origin stop ID
     * @param destinationStopId Destination stop ID
     * @param timeWindowMinutes Time window for data
     * @return List of travel times in minutes
     */
    public List<Double> queryTravelTimes(long originStopId, long destinationStopId, int timeWindowMinutes) {
        System.out.println("[DataAccessLayer] Querying travel times: " + originStopId + " -> " + destinationStopId);
        
        try {
            // Try to get data from Observer
            if (observerClient != null) {
                Object result = observerClient.queryTravelTime(originStopId, destinationStopId);
                if (result != null) {
                    return parseHistoricalTimes(result);
                }
            }
            
            // Fallback: simulate data
            System.out.println("[DataAccessLayer] Using simulated data (Observer not available)");
            return simulateTravelTimes(originStopId, destinationStopId);
            
        } catch (Exception e) {
            System.err.println("[DataAccessLayer] Error querying travel times: " + e.getMessage());
            return simulateTravelTimes(originStopId, destinationStopId);
        }
    }
    
    
    // ==================== PARSING METHODS ====================
    
    /**
     * Parse historical times from Observer response.
     */
    private List<Double> parseHistoricalTimes(Object result) {
        List<Double> times = new ArrayList<>();
        
        // TODO: Parse actual Observer response format
        // For now, return empty to trigger fallback
        
        return times;
    }
    
    // ==================== SIMULATION METHODS (FALLBACK) ====================
    
    /**
     * Simulate travel time data (fallback when real data unavailable).
     */
    private List<Double> simulateTravelTimes(long originStopId, long destinationStopId) {
        List<Double> times = new ArrayList<>();
        
        // Base time depends on distance (approximated by ID difference)
        double baseTime = 10.0 + (Math.abs(originStopId - destinationStopId) % 20);
        
        // Generate 20 samples with realistic variance
        for (int i = 0; i < 20; i++) {
            double variance = (Math.random() - 0.5) * 4; // ±2 minutes
            times.add(Math.max(1.0, baseTime + variance)); // Min 1 minute
        }
        
        return times;
    }
    
    // ==================== ARC SPEED QUERIES ====================
    
    /**
     * Query arc speeds for all arcs in a zone.
     * PRIMARY method for operation controllers.
     * 
     * @param zoneId Zone identifier
     * @param timeWindowMinutes Time window for data
     * @return Map of arcId -> list of speeds
     */
    public Map<Long, List<Double>> queryArcSpeedsInZone(String zoneId, int timeWindowMinutes) {
        System.out.println("[DataAccessLayer] Querying arc speeds for zone: " + zoneId);
        
        try {
            // Try to get data from Observer
            if (observerClient != null) {
                Object result = observerClient.queryZoneArcSpeeds(zoneId);
                if (result != null) {
                    return parseArcSpeedData(result);
                }
            }
            
            // Fallback: simulate data
            System.out.println("[DataAccessLayer] Using simulated arc speed data (Observer not available)");
            return simulateArcSpeedsInZone(zoneId);
            
        } catch (Exception e) {
            System.err.println("[DataAccessLayer] Error querying arc speeds: " + e.getMessage());
            return simulateArcSpeedsInZone(zoneId);
        }
    }
    
    /**
     * Query speed data for a specific arc.
     * @param arcId Arc identifier
     * @param timeWindowMinutes Time window for data
     * @return List of speeds in km/h
     */
    public List<Double> queryArcSpeeds(Long arcId, int timeWindowMinutes) {
        System.out.println("[DataAccessLayer] Querying speeds for arc: " + arcId);
        
        try {
            // Try to get data from Observer
            if (observerClient != null) {
                // TODO: Add arc-specific query method to Observer
                return simulateArcSpeedData(arcId);
            }
            
            return simulateArcSpeedData(arcId);
            
        } catch (Exception e) {
            System.err.println("[DataAccessLayer] Error querying arc speeds: " + e.getMessage());
            return simulateArcSpeedData(arcId);
        }
    }
    
    // ==================== PARSING METHODS ====================
    
    /**
     * Parse arc speed data from Observer response.
     */
    private Map<Long, List<Double>> parseArcSpeedData(Object result) {
        Map<Long, List<Double>> arcData = new HashMap<>();
        
        // TODO: Parse actual Observer response format
        // For now, return empty to trigger fallback
        
        return arcData;
    }
    
    // ==================== SIMULATION METHODS (FALLBACK) ====================
    
    /**
     * Simulate arc speeds in a zone (fallback when real data unavailable).
     */
    private Map<Long, List<Double>> simulateArcSpeedsInZone(String zoneId) {
        Map<Long, List<Double>> arcData = new HashMap<>();
        
        // Simulate 5-10 arcs per zone
        int zoneHash = Math.abs(zoneId.hashCode());
        int arcCount = 5 + (zoneHash % 6); // 5-10 arcs
        
        long baseArcId = (zoneHash % 1000) * 10;
        
        for (int i = 0; i < arcCount; i++) {
            long arcId = baseArcId + i;
            arcData.put(arcId, simulateArcSpeedData(arcId));
        }
        
        return arcData;
    }
    
    /**
     * Simulate speed data for a single arc.
     */
    private List<Double> simulateArcSpeedData(Long arcId) {
        List<Double> speeds = new ArrayList<>();
        
        // Base speed for arc
        double baseSpeed = 30.0 + (arcId % 25);
        
        // Adjust for time of day
        int currentHour = java.time.LocalTime.now().getHour();
        if ((currentHour >= 7 && currentHour <= 9) || (currentHour >= 17 && currentHour <= 19)) {
            baseSpeed *= 0.6; // Rush hour: 40% slower
        } else if (currentHour >= 22 || currentHour <= 5) {
            baseSpeed *= 1.4; // Night: 40% faster
        }
        
        // Generate 20 samples with realistic variance
        for (int i = 0; i < 20; i++) {
            double variance = (Math.random() - 0.5) * 8; // ±4 km/h
            speeds.add(Math.max(5.0, baseSpeed + variance));
        }
        
        return speeds;
    }
}
