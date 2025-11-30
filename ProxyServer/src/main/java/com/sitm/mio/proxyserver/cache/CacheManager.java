package com.sitm.mio.proxyserver.cache;

import com.sitm.mio.proxyserver.dto.CitizenInformation;
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
        this.storage = new CacheStorage();
        // Load TTL from configuration
        long ttl = com.sitm.mio.proxyserver.config.ConfigLoader.getCacheTTLMillis();
        this.cachePolicy = new SimpleCachePolicy(ttl);
        
        System.out.println("CacheManager initialized with TTL: " + (ttl / 60000) + " minutes");
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
    public void put(String key, CitizenInformation value) {
        CacheEntry entry = new CacheEntry(value, System.currentTimeMillis());
        storage.put(key, entry);
        cachePolicy.recordInsertion(key, entry);
    }
}