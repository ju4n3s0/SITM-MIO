// Slice definitions for Analytics/Observer module
// Defines interfaces for monitoring and analytics

module SITM {
    
    // System-wide statistics
    struct SystemStatistics {
        int totalRequests;
        int cacheHits;
        int cacheMisses;
        double cacheHitRate;
        double averageResponseTime;
        int activeConnections;
        int cacheSize;
        long timestamp;
    };
    
    // Zone-specific statistics
    struct ZoneStatistics {
        string zoneId;
        int requestCount;
        double averageResponseTime;
        int activeConnections;
        double cacheHitRate;
        long timestamp;
    };
    
    // Historical data point
    struct DataPoint {
        long timestamp;
        double value;
        string metric;
    };
    
    // Sequence of data points
    sequence<DataPoint> DataPointSeq;
    
    // Historical data response
    struct HistoricalData {
        string timeRange;
        DataPointSeq dataPoints;
        long timestamp;
    };
    
    // Analytics interface for Observer
    interface Analytics {
        /**
         * Get overall system statistics.
         * @return SystemStatistics with current metrics
         */
        SystemStatistics getSystemStatistics();
        
        /**
         * Get zone-specific statistics.
         * @param zoneId Zone identifier
         * @return ZoneStatistics for the specified zone
         */
        ZoneStatistics getZoneStatistics(string zoneId);
        
        /**
         * Get historical data for a time range.
         * @param timeRange Time range (e.g., "1h", "24h", "7d")
         * @return HistoricalData with time-series metrics
         */
        HistoricalData getHistoricalData(string timeRange);
    };
};
