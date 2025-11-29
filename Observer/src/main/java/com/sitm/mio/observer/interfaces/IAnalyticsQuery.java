package com.sitm.mio.observer.interfaces;

/**
 * Interface for querying analytics results.
 * Provides access to calculated analytics and metrics.
 * 
 * Can be realized by: AnalyticsUpdater or separate query service
 */
public interface IAnalyticsQuery {
    
    /**
     * Get current analytics summary for all zones.
     * @return Analytics summary object
     */
    Object getAnalyticsSummary();
    
    /**
     * Get analytics for a specific zone.
     * @param zoneId Zone identifier
     * @return Zone-specific analytics
     */
    Object getZoneAnalytics(String zoneId);
    
    /**
     * Get historical analytics for a time range.
     * @param timeRange Time range specification
     * @return Historical analytics data
     */
    Object getHistoricalAnalytics(String timeRange);
}
