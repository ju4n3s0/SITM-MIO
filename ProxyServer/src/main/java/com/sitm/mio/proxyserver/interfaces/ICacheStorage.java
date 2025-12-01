package com.sitm.mio.proxyserver.interfaces;

import com.sitm.mio.proxyserver.cache.CacheEntry;

public interface ICacheStorage {

    
    CacheEntry get(String key);

    
    void put(String key, CacheEntry entry);

    
    void remove(String key);
}