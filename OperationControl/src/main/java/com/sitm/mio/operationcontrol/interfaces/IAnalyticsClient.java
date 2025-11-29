package com.sitm.mio.operationcontrol.interfaces;

/**
 * Interface for querying analytics from the Observer module.
 * Contract for communication between OperationControl and Observer systems.
 * 
 * This interface defines the analytics query operations that OperationControl
 * can perform against the Observer's analytics processing system.
 * 
 * Realized by: AnalyticsClient
 */
public interface IAnalyticsClient {
    
    /**
     * Get system-wide analytics summary.
     * 
     * @return System analytics containing overall metrics
     */
    Object getSystemAnalytics();
    
    /**
     * Get analytics for a specific zone.
     * 
     * @param zoneId Zone identifier
     * @return Zone-specific analytics
     */
    Object getZoneAnalytics(String zoneId);
    
    /**
     * Get historical analytics trends.
     * 
     * @param timeRange Time range specification (e.g., "1h", "24h", "7d")
     * @return Historical trend data
     */
    Object getHistoricalTrends(String timeRange);
    
    /**
     * Get analytics for a specific arc within a zone.
     * 
     * @param zoneId Zone identifier
     * @param arcId Arc identifier
     * @return Arc-level analytics
     */
    Object getArcAnalytics(String zoneId, Long arcId);
    
    /**
     * Get Observer system performance metrics.
     * 
     * @return Performance and health metrics
     */
    Object getObserverPerformance();
    
    /**
     * Check if Observer API is reachable.
     * 
     * @return true if Observer is available
     */
    boolean isObserverAvailable();
}
