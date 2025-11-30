package proxyserver.cache;

import citizen.dto.InformacionAlCiudadano;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


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
}