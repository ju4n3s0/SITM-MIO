package com.sitm.mio.proxyserver.interfaces;

/**
 * Interface for cache policy management.
 * Defines rules for cache behavior, TTL, and eviction strategies.
 * 
 * Realized by: CachePolicy
 */
public interface ICachePolicy {
    
    /**
     * Get TTL (time-to-live) for a given cache key type.
     * 
     * @param keyType Type of cache key (e.g., "zone-stats", "system-stats")
     * @return TTL in seconds
     */
    int getTTL(String keyType);
    
    /**
     * Check if a key should be cached based on policy.
     * 
     * @param key Cache key
     * @param dataSize Data size in bytes
     * @return true if should be cached
     */
    boolean shouldCache(String key, long dataSize);
    
    /**
     * Determine if cache entry should be evicted.
     * 
     * @param key Cache key
     * @param lastAccessTime Last access timestamp
     * @return true if should be evicted
     */
    boolean shouldEvict(String key, long lastAccessTime);
    
    /**
     * Get maximum cache size in bytes.
     * 
     * @return Max size
     */
    long getMaxCacheSize();
    
    /**
     * Get eviction strategy.
     * 
     * @return Strategy name (e.g., "LRU", "LFU", "FIFO")
     */
    String getEvictionStrategy();
}
