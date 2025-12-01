package com.sitm.mio.proxyserver.cache;

import com.sitm.mio.proxyserver.interfaces.ICachePolicy;

public class SimpleCachePolicy implements ICachePolicy {

    private final long ttlMillis;

    public SimpleCachePolicy(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    @Override
    public boolean shouldEvict(String key, CacheEntry entry) {
        long now = System.currentTimeMillis();
        return (now - entry.getTimestamp()) > ttlMillis;
    }
}