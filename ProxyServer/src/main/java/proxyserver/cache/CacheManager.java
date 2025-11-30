package proxyserver.cache;

import citizen.dto.InformacionAlCiudadano;
import proxyserver.cache.CacheEntry;
import proxyserver.cache.ICachePolicy;
import proxyserver.cache.SimpleCachePolicy;

import citizen.dto.InformacionAlCiudadano;


public class CacheManager implements ICacheManagement {

    private final CacheStorage storage;
    private final ICachePolicy cachePolicy;

    
    public CacheManager() {
        this.storage = new CacheStorage();
        // TTL de 10 minutos
        this.cachePolicy = new SimpleCachePolicy(10 * 60 * 1000L);
    }

    
    public InformacionAlCiudadano get(String key) {
        CacheEntry entry = storage.get(key);
        if (entry == null) {
            return null;
        }
        if (cachePolicy.shouldEvict(key, entry)) {
            storage.remove(key);
            return null;
        }
        cachePolicy.recordAccess(key, entry);
        return entry.getValue();
    }

    
    public void put(String key, InformacionAlCiudadano value) {
        CacheEntry entry = new CacheEntry(value, System.currentTimeMillis());
        storage.put(key, entry);
        cachePolicy.recordInsertion(key, entry);
    }
}