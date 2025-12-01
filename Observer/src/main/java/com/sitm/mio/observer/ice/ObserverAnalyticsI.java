package com.sitm.mio.observer.ice;

import com.zeroc.Ice.Current;
import SITM.Analytics;
import SITM.SystemStatistics;
import SITM.ZoneStatistics;
import SITM.HistoricalData;
import com.sitm.mio.observer.component.ProxyClientICE;

/**
 * ICE servant implementation for Observer's Analytics interface.
 * Provides analytics data to OperationControl.
 * 
 * Observer acts as a proxy - it queries ProxyServer and returns the data.
 */
public class ObserverAnalyticsI implements Analytics {
    
    private final ProxyClientICE proxyClient;
    
    public ObserverAnalyticsI(ProxyClientICE proxyClient) {
        this.proxyClient = proxyClient;
        System.out.println("ObserverAnalyticsI servant created");
    }
    
    @Override
    public SystemStatistics getSystemStatistics(Current current) {
        System.out.println("ObserverAnalyticsI: getSystemStatistics called from " + current.con.toString());
        
        // Query ProxyServer and return the data
        SystemStatistics stats = proxyClient.getSystemStatistics();
        
        if (stats == null) {
            // Return empty stats if ProxyServer is unavailable
            stats = new SystemStatistics();
            stats.totalRequests = 0;
            stats.cacheHits = 0;
            stats.cacheMisses = 0;
            stats.cacheHitRate = 0.0;
            stats.averageResponseTime = 0.0;
            stats.activeConnections = 0;
        }
        
        return stats;
    }
    
    @Override
    public ZoneStatistics getZoneStatistics(String zoneId, Current current) {
        System.out.println("ObserverAnalyticsI: getZoneStatistics called for zone: " + zoneId);
        
        // Query ProxyServer and return the data
        ZoneStatistics stats = proxyClient.getZoneStatistics(zoneId);
        
        if (stats == null) {
            // Return empty stats if ProxyServer is unavailable
            stats = new ZoneStatistics();
            stats.zoneId = zoneId;
            stats.requestCount = 0;
            stats.averageResponseTime = 0.0;
            stats.activeConnections = 0;
            stats.cacheHitRate = 0.0;
        }
        
        return stats;
    }
    
    @Override
    public HistoricalData getHistoricalData(String timeRange, Current current) {
        System.out.println("ObserverAnalyticsI: getHistoricalData called for range: " + timeRange);
        
        // Query ProxyServer and return the data
        HistoricalData data = proxyClient.getHistoricalData(timeRange);
        
        if (data == null) {
            // Return empty data if ProxyServer is unavailable
            data = new HistoricalData();
            data.timeRange = timeRange;
            data.dataPoints = new SITM.DataPoint[0];
        }
        
        return data;
    }
}
