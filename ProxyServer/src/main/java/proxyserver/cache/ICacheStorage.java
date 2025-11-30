package proxyserver.cache;


public interface ICacheStorage {

    
    CacheEntry get(String key);

    
    void put(String key, CacheEntry entry);

    
    void remove(String key);
}