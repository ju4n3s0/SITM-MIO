package com.sitm.mio.proxyserver.interfaces;

import com.sitm.mio.proxyserver.cache.CacheEntry;


public interface ICachePolicy {

    
    boolean shouldEvict(String key, CacheEntry entry);

   
    default void recordInsertion(String key, CacheEntry entry) {
        // Implementaciones pueden sobrescribir según necesidad
    }

    
    default void recordAccess(String key, CacheEntry entry) {
        // Implementaciones pueden sobrescribir según necesidad
    }

   
    default String selectEvictionCandidate() {
        return null;
    }
}