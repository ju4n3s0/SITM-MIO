package com.sitm.mio.proxyserver.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sitm.mio.proxyserver.interfaces.ICacheStorage;

/**
 * Cache storage with JSON persistence support.
 * Automatically loads cache on startup and saves on shutdown.
 */
public class CacheStorage implements ICacheStorage {

    private final Map<String, CacheEntry> storage = new ConcurrentHashMap<>();
    private final CachePersistence persistence;
    private final boolean persistenceEnabled;

    /**
     * Create cache storage with persistence enabled.
     * @param ttlMillis Time-to-live for cache validation
     */
    public CacheStorage(long ttlMillis) {
        this.persistence = new CachePersistence(ttlMillis);
        this.persistenceEnabled = true;
        
        // Load cache from disk on startup
        loadCache();
        
        // Register shutdown hook to save cache
        registerShutdownHook();
    }
    
    /**
     * Create cache storage without persistence (for testing).
     */
    public CacheStorage() {
        this.persistence = null;
        this.persistenceEnabled = false;
    }

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
    
    /**
     * Get all cache entries (for persistence).
     */
    public Map<String, CacheEntry> getAllEntries() {
        return new ConcurrentHashMap<>(storage);
    }
    
    /**
     * Load cache from disk.
     */
    private void loadCache() {
        if (!persistenceEnabled) {
            return;
        }
        
        try {
            Map<String, CacheEntry> loadedEntries = persistence.load();
            storage.putAll(loadedEntries);
            System.out.println("CacheStorage: Loaded " + loadedEntries.size() + " entries from disk");
        } catch (Exception e) {
            System.err.println("CacheStorage: Failed to load cache: " + e.getMessage());
        }
    }
    
    /**
     * Save cache to disk.
     */
    public void saveCache() {
        if (!persistenceEnabled) {
            return;
        }
        
        try {
            boolean success = persistence.save(storage);
            if (success) {
                System.out.println("CacheStorage: Cache saved successfully");
            }
        } catch (Exception e) {
            System.err.println("CacheStorage: Failed to save cache: " + e.getMessage());
        }
    }
    
    /**
     * Register shutdown hook to auto-save cache.
     */
    private void registerShutdownHook() {
        if (!persistenceEnabled) {
            return;
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("CacheStorage: Saving cache on shutdown...");
            saveCache();
        }, "CacheStorage-Shutdown"));
    }
    
    /**
     * Clear all cache entries and delete persistence file.
     */
    public void clearAll() {
        storage.clear();
        if (persistenceEnabled) {
            persistence.clear();
        }
        System.out.println("CacheStorage: Cache cleared");
    }
}