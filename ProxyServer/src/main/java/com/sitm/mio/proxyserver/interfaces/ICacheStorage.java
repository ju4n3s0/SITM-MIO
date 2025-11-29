package com.sitm.mio.proxyserver.interfaces;

/**
 * Interface for physical cache storage operations.
 * Handles low-level storage and retrieval of cached data.
 * 
 * Realized by: CacheStorage
 */
public interface ICacheStorage {
    
    /**
     * Store data in physical storage.
     * 
     * @param key Storage key
     * @param value Data to store
     */
    void store(String key, Object value);
    
    /**
     * Retrieve data from physical storage.
     * 
     * @param key Storage key
     * @return Stored data, or null if not found
     */
    Object retrieve(String key);
    
    /**
     * Remove data from physical storage.
     * 
     * @param key Storage key
     */
    void remove(String key);
    
    /**
     * Check if key exists in storage.
     * 
     * @param key Storage key
     * @return true if exists
     */
    boolean exists(String key);
    
    /**
     * Clear all storage.
     */
    void clearAll();
    
    /**
     * Get storage size in bytes.
     * 
     * @return Size in bytes
     */
    long getSize();
}
