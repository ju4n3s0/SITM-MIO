package com.sitm.mio.proxyserver.cache;

import SITM.CitizenInformation;

/**
 * Cache entry storing data with timestamp and type.
 * Supports both Citizen queries and Operator analytics.
 * Used for cache expiration and eviction policies.
 */
public class CacheEntry {
    private final Object value;
    private final long timestamp;
    private final CacheType type;

    /**
     * Create a cache entry with type information.
     * @param value The cached data (CitizenInformation, SystemStatistics, etc.)
     * @param timestamp When the entry was created
     * @param type The type of cached data
     */
    public CacheEntry(Object value, long timestamp, CacheType type) {
        this.value = value;
        this.timestamp = timestamp;
        this.type = type;
    }
    
    /**
     * Create a cache entry for CitizenInformation (backward compatibility).
     * @param value CitizenInformation
     * @param timestamp When the entry was created
     */
    public CacheEntry(CitizenInformation value, long timestamp) {
        this(value, timestamp, CacheType.CITIZEN);
    }

    /**
     * Get the cached value.
     * @return The cached object
     */
    public Object getRawValue() {
        return value;
    }
    
    /**
     * Get the cached value as CitizenInformation.
     * @return CitizenInformation or null if wrong type
     */
    public CitizenInformation getValue() {
        if (value instanceof CitizenInformation) {
            return (CitizenInformation) value;
        }
        return null;
    }

    /**
     * Get the timestamp when this entry was created.
     * @return Timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Get the cache type.
     * @return CacheType
     */
    public CacheType getType() {
        return type;
    }
    
    /**
     * Check if this entry is for citizen data.
     */
    public boolean isCitizenData() {
        return type == CacheType.CITIZEN;
    }
    
    /**
     * Check if this entry is for operator analytics.
     */
    public boolean isOperatorData() {
        return type == CacheType.SYSTEM_STATS || 
               type == CacheType.ZONE_DATAGRAM || 
               type == CacheType.HISTORICAL;
    }
}