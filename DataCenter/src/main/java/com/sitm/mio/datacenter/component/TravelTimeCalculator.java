package com.sitm.mio.datacenter.component;

import com.sitm.mio.datacenter.interfaces.ITravelTimeStatsRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Travel Time Calculator for DataCenter.
 * Calculates average travel times between stops for citizen queries.
 * 
 * This is the DataCenter's implementation of travel time calculation,
 * separate from OperationControl's CalculateAverageTime which is for operators.
 */
public class TravelTimeCalculator {
    
    private final ITravelTimeStatsRepository travelTimeRepo;
    
    public TravelTimeCalculator(ITravelTimeStatsRepository travelTimeRepo) {
        this.travelTimeRepo = travelTimeRepo;
    }
    
    /**
     * Calculate average travel time between two stops.
     * 
     * @param originStopId Origin stop ID
     * @param destinationStopId Destination stop ID
     * @return Formatted message with travel time information
     */
    public String calculateTravelTime(long originStopId, long destinationStopId) {
        System.out.println("[TravelTimeCalculator] Calculating travel time: " + 
                          originStopId + " -> " + destinationStopId);
        
        try {
            // Query historical travel time data
            List<Double> travelTimes = queryTravelTimes(originStopId, destinationStopId);
            
            if (travelTimes.isEmpty()) {
                // Fallback to simulation
                System.out.println("[TravelTimeCalculator] No historical data, using simulation");
                travelTimes = simulateTravelTimes(originStopId, destinationStopId);
            }
            
            // Calculate statistics
            double average = calculateAverage(travelTimes);
            double min = travelTimes.stream().min(Double::compare).orElse(0.0);
            double max = travelTimes.stream().max(Double::compare).orElse(0.0);
            
            // Format response message
            String message = String.format(
                "Travel time from stop %d to stop %d:\n" +
                "  Average: %.1f minutes\n" +
                "  Range: %.1f - %.1f minutes\n" +
                "  Based on %d samples",
                originStopId, destinationStopId,
                average, min, max, travelTimes.size()
            );
            
            System.out.println("[TravelTimeCalculator] Result: " + average + " minutes (avg)");
            return message;
            
        } catch (Exception e) {
            System.err.println("[TravelTimeCalculator] Error: " + e.getMessage());
            return String.format(
                "Unable to calculate travel time from stop %d to stop %d. Please try again later.",
                originStopId, destinationStopId
            );
        }
    }
    
    /**
     * Query historical travel times from repository.
     */
    private List<Double> queryTravelTimes(long originStopId, long destinationStopId) {
        try {
            // Query actual travel time data from repository
            if (travelTimeRepo instanceof TravelTimeStatsRepository) {
                TravelTimeStatsRepository repo = (TravelTimeStatsRepository) travelTimeRepo;
                return repo.queryTravelTimes(originStopId, destinationStopId);
            }
        } catch (Exception e) {
            System.err.println("[TravelTimeCalculator] Error querying data: " + e.getMessage());
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Simulate travel times (fallback when no historical data available).
     */
    private List<Double> simulateTravelTimes(long originStopId, long destinationStopId) {
        List<Double> times = new ArrayList<>();
        
        // Base time depends on stop distance (simulated)
        double baseTime = 10.0 + (Math.abs(originStopId - destinationStopId) % 20);
        
        // Adjust for time of day
        int currentHour = java.time.LocalTime.now().getHour();
        if ((currentHour >= 7 && currentHour <= 9) || (currentHour >= 17 && currentHour <= 19)) {
            baseTime *= 1.3; // Rush hour: 30% slower
        } else if (currentHour >= 22 || currentHour <= 5) {
            baseTime *= 0.8; // Night: 20% faster
        }
        
        // Generate 20 samples with realistic variance
        for (int i = 0; i < 20; i++) {
            double variance = (Math.random() - 0.5) * 4; // ±2 minutes
            times.add(Math.max(1.0, baseTime + variance)); // Min 1 minute
        }
        
        return times;
    }
    
    /**
     * Calculate average of a list of values.
     */
    private double calculateAverage(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        
        return sum / values.size();
    }
}
