package com.sitm.mio.proxyserver.analytics;

import com.sitm.mio.proxyserver.cache.CacheManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for collecting and providing analytics data.
 * Aggregates statistics from various ProxyServer components.
 */
public class AnalyticsService {
    
    private final StatisticsCollector globalStats;
    private final Map<String, StatisticsCollector> zoneStats;
    private final CacheManager cacheManager;
    
    public AnalyticsService(CacheManager cacheManager) {
        this.globalStats = new StatisticsCollector();
        this.zoneStats = new ConcurrentHashMap<>();
        this.cacheManager = cacheManager;
        
        System.out.println("AnalyticsService initialized");
    }
    
    /**
     * Get global statistics collector.
     */
    public StatisticsCollector getGlobalStats() {
        return globalStats;
    }
    
    /**
     * Get statistics collector for a specific zone.
     * Creates a new collector if zone doesn't exist.
     */
    public StatisticsCollector getZoneStats(String zoneId) {
        return zoneStats.computeIfAbsent(zoneId, k -> new StatisticsCollector());
    }
    
    /**
     * Get system-wide statistics as a map.
     */
    public Map<String, Object> getSystemStatistics() {
        var stats = new HashMap<String, Object>();
        stats.put("totalRequests", globalStats.getTotalRequests());
        stats.put("cacheHits", globalStats.getCacheHits());
        stats.put("cacheMisses", globalStats.getCacheMisses());
        stats.put("cacheHitRate", globalStats.getCacheHitRate());
        stats.put("averageResponseTime", globalStats.getAverageResponseTime());
        stats.put("activeConnections", globalStats.getActiveConnections());
        stats.put("timestamp", System.currentTimeMillis());
        
        // Add cache manager stats if available
        if (cacheManager != null) {
            stats.put("cacheSize", cacheManager.getCacheSize());
        }
        
        return stats;
    }
    
    /**
     * Get zone-specific statistics as a map.
     */
    public Map<String, Object> getZoneStatistics(String zoneId) {
        var collector = zoneStats.get(zoneId);
        
        if (collector == null) {
            // Return empty stats for unknown zone
            var stats = new HashMap<String, Object>();
            stats.put("zoneId", zoneId);
            stats.put("requestCount", 0);
            stats.put("averageResponseTime", 0.0);
            stats.put("activeConnections", 0);
            stats.put("timestamp", System.currentTimeMillis());
            return stats;
        }
        
        var stats = new HashMap<String, Object>();
        stats.put("zoneId", zoneId);
        stats.put("requestCount", collector.getTotalRequests());
        stats.put("averageResponseTime", collector.getAverageResponseTime());
        stats.put("activeConnections", collector.getActiveConnections());
        stats.put("cacheHitRate", collector.getCacheHitRate());
        stats.put("timestamp", System.currentTimeMillis());
        
        return stats;
    }
    
    /**
     * Get historical data for a time range.
     * For now, returns current snapshot. Future: implement time-series storage.
     */
    public Map<String, Object> getHistoricalData(String timeRange) {
        var data = new HashMap<String, Object>();
        data.put("timeRange", timeRange);
        data.put("timestamp", System.currentTimeMillis());
        
        // For now, return current stats as a single data point
        // Future: implement actual time-series data collection
        var dataPoints = new java.util.ArrayList<Map<String, Object>>();
        
        var point = new HashMap<String, Object>();
        point.put("timestamp", System.currentTimeMillis());
        point.put("value", globalStats.getCacheHitRate());
        point.put("metric", "cache_hit_rate");
        dataPoints.add(point);
        
        point = new HashMap<String, Object>();
        point.put("timestamp", System.currentTimeMillis());
        point.put("value", globalStats.getAverageResponseTime());
        point.put("metric", "avg_response_time");
        dataPoints.add(point);
        
        point = new HashMap<String, Object>();
        point.put("timestamp", System.currentTimeMillis());
        point.put("value", globalStats.getTotalRequests());
        point.put("metric", "total_requests");
        dataPoints.add(point);
        
        data.put("dataPoints", dataPoints);
        
        return data;
    }
    
    /**
     * Get all available zones.
     */
    public java.util.Set<String> getAvailableZones() {
        return zoneStats.keySet();
    }
    
    /**
     * Print current statistics summary.
     */
    public void printSummary() {
        System.out.println("\n=== Analytics Summary ===");
        System.out.println("Global: " + globalStats);
        System.out.println("Zones tracked: " + zoneStats.size());
        for (var entry : zoneStats.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("========================\n");
    }
}
