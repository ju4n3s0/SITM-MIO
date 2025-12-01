package com.sitm.mio.proxyserver.ice;

import com.zeroc.Ice.Current;
import SITM.*;
import com.sitm.mio.proxyserver.analytics.AnalyticsService;

import java.util.Map;

/**
 * ICE servant implementation for Analytics interface.
 * Handles RPC calls from Observer module for monitoring/analytics.
 */
public class AnalyticsI implements Analytics {
    
    private final AnalyticsService analyticsService;
    
    public AnalyticsI(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
        System.out.println("AnalyticsI servant created");
    }
    
    @Override
    public SystemStatistics getSystemStatistics(Current current) {
        System.out.println("ICE RPC: getSystemStatistics()");
        
        Map<String, Object> stats = analyticsService.getSystemStatistics();
        
        var result = new SystemStatistics();
        result.totalRequests = ((Number) stats.get("totalRequests")).intValue();
        result.cacheHits = ((Number) stats.get("cacheHits")).intValue();
        result.cacheMisses = ((Number) stats.get("cacheMisses")).intValue();
        result.cacheHitRate = ((Number) stats.get("cacheHitRate")).doubleValue();
        result.averageResponseTime = ((Number) stats.get("averageResponseTime")).doubleValue();
        result.activeConnections = ((Number) stats.get("activeConnections")).intValue();
        result.cacheSize = ((Number) stats.get("cacheSize")).intValue();
        result.timestamp = ((Number) stats.get("timestamp")).longValue();
        
        return result;
    }
    
    @Override
    public ZoneStatistics getZoneStatistics(String zoneId, Current current) {
        System.out.println("ICE RPC: getZoneStatistics(" + zoneId + ")");
        
        Map<String, Object> stats = analyticsService.getZoneStatistics(zoneId);
        
        var result = new ZoneStatistics();
        result.zoneId = (String) stats.get("zoneId");
        result.requestCount = ((Number) stats.get("requestCount")).intValue();
        result.averageResponseTime = ((Number) stats.get("averageResponseTime")).doubleValue();
        result.activeConnections = ((Number) stats.get("activeConnections")).intValue();
        result.cacheHitRate = ((Number) stats.get("cacheHitRate")).doubleValue();
        result.timestamp = ((Number) stats.get("timestamp")).longValue();
        
        return result;
    }
    
    @Override
    public HistoricalData getHistoricalData(String timeRange, Current current) {
        System.out.println("ICE RPC: getHistoricalData(" + timeRange + ")");
        
        Map<String, Object> data = analyticsService.getHistoricalData(timeRange);
        
        var result = new HistoricalData();
        result.timeRange = (String) data.get("timeRange");
        result.timestamp = ((Number) data.get("timestamp")).longValue();
        
        // Convert data points
        @SuppressWarnings("unchecked")
        var dataPoints = (java.util.List<Map<String, Object>>) data.get("dataPoints");
        result.dataPoints = new DataPoint[dataPoints.size()];
        
        for (int i = 0; i < dataPoints.size(); i++) {
            var point = dataPoints.get(i);
            var dp = new DataPoint();
            dp.timestamp = ((Number) point.get("timestamp")).longValue();
            dp.value = ((Number) point.get("value")).doubleValue();
            dp.metric = (String) point.get("metric");
            result.dataPoints[i] = dp;
        }
        
        return result;
    }
}
