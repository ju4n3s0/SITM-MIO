package com.sitm.mio.proxyserver.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sitm.mio.proxyserver.interfaces.ICacheStorage;


public class CacheStorage implements ICacheStorage {

    
    private final Map<String, CacheEntry> storage = new ConcurrentHashMap<>();

    @Override
    public CacheEntry get(String key) {
        return storage.get(key);
    }

    
    @Override
    public void put(String key, CacheEntry entry) {
        storage.put(key, entry);
    }

    
    @Override
    public void remove(String key) {
        storage.remove(key);
    }
    
    /**
     * Get the current number of entries in the cache.
     */
    public int size() {
        return storage.size();
    }
}