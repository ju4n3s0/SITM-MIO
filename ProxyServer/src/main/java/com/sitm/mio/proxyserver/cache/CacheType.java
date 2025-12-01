package com.sitm.mio.proxyserver.cache;

/**
 * Enum defining different types of cached data.
 * Used to differentiate between Citizen and Operator cache entries.
 */
public enum CacheType {
    /**
     * Citizen queries - travel time information between stops.
     * Key format: "CITIZEN:{originId}:{destinationId}"
     */
    CITIZEN("CITIZEN"),
    
    /**
     * System statistics - overall system metrics for operators.
     * Key format: "SYSTEM_STATS"
     */
    SYSTEM_STATS("SYSTEM_STATS"),
    
    /**
     * Zone Datagram - zone-specific metrics for operators.
     * Key format: "ZONE_STATS:{zoneId}"
     */
    ZONE_DATAGRAM("ZONE_DATAGRAM"),
    
    /**
     * Historical data - time-series data for operators.
     * Key format: "HISTORICAL:{timeRange}"
     */
    HISTORICAL("HISTORICAL");
    
    private final String prefix;
    
    CacheType(String prefix) {
        this.prefix = prefix;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    /**
     * Create a cache key with the appropriate prefix.
     * @param parts Key components (e.g., originId, destinationId)
     * @return Formatted cache key
     */
    public String createKey(String... parts) {
        if (parts.length == 0) {
            return prefix;
        }
        return prefix + ":" + String.join(":", parts);
    }
    
    /**
     * Extract cache type from a cache key.
     * @param key The cache key
     * @return CacheType or null if invalid
     */
    public static CacheType fromKey(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        
        for (CacheType type : values()) {
            if (key.startsWith(type.prefix + ":") || key.equals(type.prefix)) {
                return type;
            }
        }
        return null;
    }
}
