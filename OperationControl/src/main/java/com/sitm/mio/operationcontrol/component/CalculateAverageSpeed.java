package com.sitm.mio.operationcontrol.component;

import com.sitm.mio.operationcontrol.interfaces.ITaskDelegator;
import com.sitm.mio.operationcontrol.interfaces.IResultReceiver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Worker strategy component for calculating average speeds per arc.
 * Part of the Master-Slave calculation pattern.
 * Component from deployment diagram: CalcularVelocidadPromedio
 * 
 * REQUIREMENT: Allow 40 operation controllers to visualize average speed per arc
 * in their assigned zones in real-time.
 * 
 * Uses: ITaskDelegator (to interact with WorkerCalculator master)
 * Uses: IResultReceiver (to send results back)
 */
public class CalculateAverageSpeed {
    
    private final ITaskDelegator taskDelegator;
    private final IResultReceiver resultReceiver;
    private final DataAccessLayer dataAccess;
    
    public CalculateAverageSpeed(ITaskDelegator taskDelegator, IResultReceiver resultReceiver, DataAccessLayer dataAccess) {
        this.taskDelegator = taskDelegator;
        this.resultReceiver = resultReceiver;
        this.dataAccess = dataAccess;
    }
    
    /**
     * Calculate average speed for all arcs in a zone.
     * This is the PRIMARY method for operation controllers.
     * 
     * @param zoneId Zone identifier (e.g., "Z2_5")
     * @return Map of arcId -> speed statistics
     */
    public Map<String, Object> calculateArcSpeedsForZone(String zoneId) {
        System.out.println("[CalculateAverageSpeed] Calculating arc speeds for zone: " + zoneId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("zoneId", zoneId);
        
        try {
            // Query arc speed data for the zone
            Map<Long, List<Double>> arcSpeedData = dataAccess != null
                ? dataAccess.queryArcSpeedsInZone(zoneId, 60)
                : simulateArcSpeedsInZone(zoneId);
            
            if (arcSpeedData.isEmpty()) {
                result.put("status", "NO_DATA");
                result.put("arcCount", 0);
                return result;
            }
            
            // Calculate average speed for each arc
            Map<String, Map<String, Object>> arcResults = new HashMap<>();
            
            for (Map.Entry<Long, List<Double>> entry : arcSpeedData.entrySet()) {
                Long arcId = entry.getKey();
                List<Double> speeds = entry.getValue();
                
                if (!speeds.isEmpty()) {
                    Map<String, Object> arcStats = calculateArcStatistics(arcId, speeds);
                    arcResults.put("ARC_" + arcId, arcStats);
                }
            }
            
            result.put("status", "SUCCESS");
            result.put("arcCount", arcResults.size());
            result.put("arcSpeeds", arcResults);
            result.put("timestamp", System.currentTimeMillis());
            
            System.out.println(String.format("[CalculateAverageSpeed] Zone %s: %d arcs processed",
                zoneId, arcResults.size()));
            
            // Send result to receiver if available
            if (resultReceiver != null) {
                resultReceiver.receiveResult("ARC_SPEEDS_ZONE", result);
            }
            
        } catch (Exception e) {
            System.err.println("[CalculateAverageSpeed] Error: " + e.getMessage());
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Calculate average speed for a specific arc.
     * @param arcId Arc identifier
     * @return Average speed result
     */
    public Map<String, Object> calculateArcSpeed(Long arcId) {
        System.out.println("[CalculateAverageSpeed] Calculating for arc: " + arcId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("arcId", arcId);
        
        try {
            // Query arc-specific speed data
            List<Double> speeds = dataAccess != null
                ? dataAccess.queryArcSpeeds(arcId, 60)
                : simulateArcSpeedData(arcId);
            
            if (speeds.isEmpty()) {
                result.put("status", "NO_DATA");
                return result;
            }
            
            Map<String, Object> stats = calculateArcStatistics(arcId, speeds);
            result.putAll(stats);
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            System.err.println("[CalculateAverageSpeed] Error: " + e.getMessage());
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Calculate statistics for an arc's speed data.
     */
    private Map<String, Object> calculateArcStatistics(Long arcId, List<Double> speeds) {
        Map<String, Object> stats = new HashMap<>();
        
        // Calculate weighted average (more recent data has higher weight)
        double weightedSum = 0;
        double totalWeight = 0;
        
        for (int i = 0; i < speeds.size(); i++) {
            double weight = 1.0 + (i * 0.1); // Recent data weighted higher
            weightedSum += speeds.get(i) * weight;
            totalWeight += weight;
        }
        
        double average = weightedSum / totalWeight;
        double min = speeds.stream().min(Double::compare).orElse(0.0);
        double max = speeds.stream().max(Double::compare).orElse(0.0);
        
        stats.put("arcId", arcId);
        stats.put("averageSpeedKmh", Math.round(average * 100.0) / 100.0);
        stats.put("minSpeedKmh", Math.round(min * 100.0) / 100.0);
        stats.put("maxSpeedKmh", Math.round(max * 100.0) / 100.0);
        stats.put("sampleCount", speeds.size());
        stats.put("congestionLevel", calculateCongestionLevel(average));
        stats.put("trafficCondition", getTrafficCondition(average));
        
        return stats;
    }
    
    /**
     * Calculate congestion level based on average speed.
     */
    private String calculateCongestionLevel(double avgSpeed) {
        if (avgSpeed >= 40) return "LOW";
        if (avgSpeed >= 25) return "MEDIUM";
        if (avgSpeed >= 15) return "HIGH";
        return "SEVERE";
    }
    
    /**
     * Get traffic condition description.
     */
    private String getTrafficCondition(double speed) {
        if (speed >= 40) return "LIGHT";
        if (speed >= 25) return "MODERATE";
        if (speed >= 15) return "HEAVY";
        return "CONGESTED";
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
