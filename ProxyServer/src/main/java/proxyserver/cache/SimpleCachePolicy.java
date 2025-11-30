package proxyserver.cache;


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