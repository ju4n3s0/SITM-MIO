package com.sitm.mio.proxyserver.component;

import com.sitm.mio.proxyserver.interfaces.ICacheStorage;
import com.sitm.mio.proxyserver.interfaces.ICachePolicy;

/**
 * Cache coordinator managing storage and policy.
 * Component from deployment diagram: CacheManager
 * 
 * Uses: ICacheStorage, ICachePolicy
 * 
 * Responsibilities:
 * - Coordinate caching operations
 * - Apply caching policies
 * - Manage cache lifecycle
 * - Provide unified cache interface
 */
public class CacheManager {
    
    private final ICacheStorage storage;
    private final ICachePolicy policy;
    
    public CacheManager(ICacheStorage storage, ICachePolicy policy) {
        this.storage = storage;
        this.policy = policy;
    }
    
    public void put(String key, Object value) {
        // TODO: Implement cache storage with policy
        // 1. Check policy.shouldCache(key, size)
        // 2. Get TTL from policy.getTTL(keyType)
        // 3. Store in storage with expiration
        System.out.println("Caching data with key: " + key);
        storage.store(key, value);
    }
    
    public Object get(String key) {
        // TODO: Implement cache retrieval
        // 1. Check if exists in storage
        // 2. Check if expired based on policy
        // 3. Return value or null
        System.out.println("Retrieving from cache: " + key);
        return storage.retrieve(key);
    }
    
    public void invalidate(String key) {
        // TODO: Implement cache invalidation
        storage.remove(key);
        System.out.println("Invalidating cache key: " + key);
    }
    
    public void clear() {
        // TODO: Implement cache clear
        storage.clearAll();
        System.out.println("Clearing all cache");
    }
    
    public boolean contains(String key) {
        // TODO: Implement cache check
        return storage.exists(key);
    }
    
    public void evictIfNeeded() {
        // TODO: Run eviction based on policy
        // 1. Check cache size against policy.getMaxCacheSize()
        // 2. Apply eviction strategy from policy.getEvictionStrategy()
        System.out.println("Running cache eviction");
    }
}
