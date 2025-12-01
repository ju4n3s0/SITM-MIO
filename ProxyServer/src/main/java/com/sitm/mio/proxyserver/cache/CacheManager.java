package com.sitm.mio.proxyserver.cache;

import SITM.CitizenInformation;
import com.sitm.mio.proxyserver.interfaces.ICacheManagement;
import com.sitm.mio.proxyserver.interfaces.ICachePolicy;

/**
 * Cache manager responsible for storing and retrieving cached citizen information.
 * Implements caching policies including TTL-based eviction.
 */
public class CacheManager implements ICacheManagement {

    private final CacheStorage storage;
    private final ICachePolicy cachePolicy;

    /**
     * Create a cache manager with TTL from configuration.
     */
    public CacheManager() {
        // Load TTL from configuration
        long ttl = com.sitm.mio.proxyserver.config.ConfigLoader.getCacheTTLMillis();
        
        // Create storage with persistence enabled
        this.storage = new CacheStorage(ttl);
        this.cachePolicy = new SimpleCachePolicy(ttl);
        
        System.out.println("CacheManager initialized with TTL: " + (ttl / 60000) + " minutes");
        System.out.println("  JSON persistence enabled: cache/proxyserver-cache.json");
    }

    /**
     * Get cached citizen information by key.
     * @param key The cache key
     * @return CitizenInformation or null if not found or expired
     */
    public CitizenInformation get(String key) {
        CacheEntry entry = storage.get(key);
        if (entry == null) {
            return null;
        }
        if (cachePolicy.shouldEvict(key, entry)) {
            storage.remove(key);
            return null;
        }
        cachePolicy.recordAccess(key, entry);
        return entry.getValue();
    }

    /**
     * Store citizen information in cache.
     * @param key The cache key
     * @param value The citizen information to cache
     */
    public void put(String key, CitizenInformation value, CacheType cacheType) {
        CacheEntry entry = new CacheEntry(value, System.currentTimeMillis(), cacheType);
        storage.put(key, entry);
        cachePolicy.recordInsertion(key, entry);
    }
    
    /**
     * Store any object in cache (for operator analytics, enriched datagrams, etc.).
     * @param key The cache key
     * @param value The object to cache
     * @param cacheType The type of cached data
     */
    public void put(String key, Object value, CacheType cacheType) {
        CacheEntry entry = new CacheEntry(value, System.currentTimeMillis(), cacheType);
        storage.put(key, entry);
        cachePolicy.recordInsertion(key, entry);
    }
    
    /**
     * Get the current number of entries in the cache.
     * @return Number of cached entries
     */
    public int getCacheSize() {
        return storage.size();
    }
}