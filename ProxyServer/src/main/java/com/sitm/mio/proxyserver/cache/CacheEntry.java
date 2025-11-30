package com.sitm.mio.proxyserver.cache;

import com.sitm.mio.proxyserver.dto.CitizenInformation;

/**
 * Cache entry storing citizen information with timestamp.
 * Used for cache expiration and eviction policies.
 */
public class CacheEntry {
    private final CitizenInformation value;
    private final long timestamp;

    public CacheEntry(CitizenInformation value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    /**
     * Get the cached value.
     * @return CitizenInformation
     */
    public CitizenInformation getValue() {
        return value;
    }

    /**
     * Get the timestamp when this entry was created.
     * @return Timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }
}