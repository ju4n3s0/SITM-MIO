package com.sitm.mio.observer.component;

import com.sitm.mio.observer.interfaces.IAnalyticsProcessor;
import com.sitm.mio.observer.interfaces.IAnalyticsQuery;
import com.sitm.mio.observer.model.BusPositionUpdatedEvent;
import com.sitm.mio.observer.model.ZoneStatisticsUpdatedEvent;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Real-time analytics processor for SITM-MIO bus data.
 * Component from deployment diagram: ActualizadorAnalíticoEnTiempoReal
 * 
 * Realizes: IAnalyticsProcessor, IAnalyticsQuery
 * 
 * Responsibilities:
 * - Receive real-time events from EventReceiver
 * - Process and aggregate analytics data
 * - Calculate system-wide metrics
 * - Store analytics results
 * - Generate reports and dashboards
 * 
 * Analytics Types:
 * - Average speeds by zone
 * - Vehicle distribution
 * - Traffic patterns
 * - Performance metrics
 */
public class AnalyticsUpdater implements IAnalyticsProcessor, IAnalyticsQuery {
    
    private final EventReceiver eventReceiver;
    
    // Analytics storage
    private final Map<String, ZoneAnalytics> zoneAnalyticsMap;
    private final Map<Integer, BusTracking> busTrackingMap;
    private final List<BusPositionUpdatedEvent> recentPositions;
    private final List<ZoneStatisticsUpdatedEvent> recentZoneStats;
    
    // System-wide metrics
    private int totalEventsProcessed;
    private LocalDateTime lastUpdateTime;
    private boolean running;
    
    public AnalyticsUpdater(EventReceiver eventReceiver) {
        this.eventReceiver = eventReceiver;
        this.zoneAnalyticsMap = new ConcurrentHashMap<>();
        this.busTrackingMap = new ConcurrentHashMap<>();
        this.recentPositions = Collections.synchronizedList(new ArrayList<>());
        this.recentZoneStats = Collections.synchronizedList(new ArrayList<>());
        this.totalEventsProcessed = 0;
        this.running = false;
    }
    
    @Override
    public void start() {
        System.out.println("Starting AnalyticsUpdater...");
        running = true;
        
        // Register event handlers with EventReceiver
        eventReceiver.onEvent((Consumer<Object>) event -> {
            if (event instanceof BusPositionUpdatedEvent) {
                handleBusPositionUpdate((BusPositionUpdatedEvent) event);
            } else if (event instanceof ZoneStatisticsUpdatedEvent) {
                handleZoneStatisticsUpdate((ZoneStatisticsUpdatedEvent) event);
            }
        });
        
        System.out.println("AnalyticsUpdater started and listening for events");
    }
    
    @Override
    public void handleBusPositionUpdate(BusPositionUpdatedEvent event) {
        if (!running) return;
        
        try {
            // Update bus tracking
            BusTracking tracking = busTrackingMap.computeIfAbsent(
                event.getBusId(),
                id -> new BusTracking(id)
            );
            tracking.updatePosition(event);
            
            // Update zone analytics
            ZoneAnalytics zoneAnalytics = zoneAnalyticsMap.computeIfAbsent(
                event.getZoneId(),
                id -> new ZoneAnalytics(id)
            );
            zoneAnalytics.addBusPosition(event);
            
            // Store recent position (keep last 1000)
            recentPositions.add(event);
            if (recentPositions.size() > 1000) {
                recentPositions.remove(0);
            }
            
            totalEventsProcessed++;
            lastUpdateTime = LocalDateTime.now();
            
            System.out.println("Processed bus position: Bus " + event.getBusId() + 
                             " in zone " + event.getZoneId() + " @ " + event.getSpeed() + " km/h");
            
        } catch (Exception e) {
            System.err.println("Error processing bus position: " + e.getMessage());
        }
    }
    
    @Override
    public void handleZoneStatisticsUpdate(ZoneStatisticsUpdatedEvent event) {
        if (!running) return;
        
        try {
            // Update zone analytics with statistics
            ZoneAnalytics zoneAnalytics = zoneAnalyticsMap.computeIfAbsent(
                event.getZoneId(),
                id -> new ZoneAnalytics(id)
            );
            zoneAnalytics.updateStatistics(event);
            
            // Store recent zone stats (keep last 500)
            recentZoneStats.add(event);
            if (recentZoneStats.size() > 500) {
                recentZoneStats.remove(0);
            }
            
            totalEventsProcessed++;
            lastUpdateTime = LocalDateTime.now();
            
            System.out.println("Processed zone statistics: Zone " + event.getZoneId() + 
                             " - Avg Speed: " + event.getAvgSpeed() + " km/h, Vehicles: " + event.getVehicleCount());
            
        } catch (Exception e) {
            System.err.println("Error processing zone statistics: " + e.getMessage());
        }
    }
    
    @Override
    public void stop() {
        System.out.println("Stopping AnalyticsUpdater...");
        running = false;
        System.out.println("AnalyticsUpdater stopped. Total events processed: " + totalEventsProcessed);
    }
    
    // IAnalyticsQuery implementation
    
    @Override
    public Object getAnalyticsSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalZones", zoneAnalyticsMap.size());
        summary.put("totalBuses", busTrackingMap.size());
        summary.put("totalEventsProcessed", totalEventsProcessed);
        summary.put("lastUpdateTime", lastUpdateTime);
        
        // Calculate system-wide average speed
        double avgSpeed = zoneAnalyticsMap.values().stream()
            .mapToDouble(ZoneAnalytics::getAverageSpeed)
            .filter(speed -> speed > 0)
            .average()
            .orElse(0.0);
        summary.put("systemAverageSpeed", avgSpeed);
        
        // Total vehicles across all zones
        int totalVehicles = zoneAnalyticsMap.values().stream()
            .mapToInt(ZoneAnalytics::getVehicleCount)
            .sum();
        summary.put("totalVehicles", totalVehicles);
        
        return summary;
    }
    
    @Override
    public Object getZoneAnalytics(String zoneId) {
        ZoneAnalytics analytics = zoneAnalyticsMap.get(zoneId);
        if (analytics == null) {
            return null;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("zoneId", zoneId);
        result.put("averageSpeed", analytics.getAverageSpeed());
        result.put("vehicleCount", analytics.getVehicleCount());
        result.put("totalPositionUpdates", analytics.getTotalUpdates());
        result.put("lastUpdate", analytics.getLastUpdateTime());
        
        return result;
    }
    
    @Override
    public Object getHistoricalAnalytics(String timeRange) {
        Map<String, Object> historical = new HashMap<>();
        historical.put("timeRange", timeRange);
        historical.put("recentPositionCount", recentPositions.size());
        historical.put("recentZoneStatsCount", recentZoneStats.size());
        
        // Group recent positions by zone
        Map<String, Long> positionsByZone = recentPositions.stream()
            .collect(Collectors.groupingBy(
                BusPositionUpdatedEvent::getZoneId,
                Collectors.counting()
            ));
        historical.put("positionsByZone", positionsByZone);
        
        return historical;
    }
    
    /**
     * Get analytics for all zones.
     */
    public Map<String, Object> getAllZoneAnalytics() {
        Map<String, Object> allZones = new HashMap<>();
        for (Map.Entry<String, ZoneAnalytics> entry : zoneAnalyticsMap.entrySet()) {
            allZones.put(entry.getKey(), getZoneAnalytics(entry.getKey()));
        }
        return allZones;
    }
    
    /**
     * Get performance metrics for the Observer system itself.
     */
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("running", running);
        metrics.put("totalEventsProcessed", totalEventsProcessed);
        metrics.put("zonesTracked", zoneAnalyticsMap.size());
        metrics.put("busesTracked", busTrackingMap.size());
        metrics.put("recentPositionsBufferSize", recentPositions.size());
        metrics.put("recentZoneStatsBufferSize", recentZoneStats.size());
        metrics.put("lastUpdateTime", lastUpdateTime);
        return metrics;
    }
    
    // Inner classes for analytics storage
    
    private static class ZoneAnalytics {
        private final String zoneId;
        private final List<Double> speeds;
        private int vehicleCount;
        private int totalUpdates;
        private LocalDateTime lastUpdateTime;
        
        public ZoneAnalytics(String zoneId) {
            this.zoneId = zoneId;
            this.speeds = Collections.synchronizedList(new ArrayList<>());
            this.vehicleCount = 0;
            this.totalUpdates = 0;
        }
        
        public void addBusPosition(BusPositionUpdatedEvent event) {
            if (event.getSpeed() != null) {
                speeds.add(event.getSpeed());
                if (speeds.size() > 100) { // Keep last 100 speeds
                    speeds.remove(0);
                }
            }
            totalUpdates++;
            lastUpdateTime = LocalDateTime.now();
        }
        
        public void updateStatistics(ZoneStatisticsUpdatedEvent event) {
            if (event.getVehicleCount() != null) {
                this.vehicleCount = event.getVehicleCount();
            }
            lastUpdateTime = LocalDateTime.now();
        }
        
        public double getAverageSpeed() {
            if (speeds.isEmpty()) return 0.0;
            return speeds.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        }
        
        public int getVehicleCount() {
            return vehicleCount;
        }
        
        public int getTotalUpdates() {
            return totalUpdates;
        }
        
        public LocalDateTime getLastUpdateTime() {
            return lastUpdateTime;
        }
    }
    
    private static class BusTracking {
        private final Integer busId;
        private String currentZone;
        private Double lastSpeed;
        private LocalDateTime lastSeen;
        
        public BusTracking(Integer busId) {
            this.busId = busId;
        }
        
        public void updatePosition(BusPositionUpdatedEvent event) {
            this.currentZone = event.getZoneId();
            this.lastSpeed = event.getSpeed();
            this.lastSeen = LocalDateTime.now();
        }
    }
}
