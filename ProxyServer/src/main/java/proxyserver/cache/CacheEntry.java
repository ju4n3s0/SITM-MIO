package proxyserver.cache;

import citizen.dto.InformacionAlCiudadano;


public class CacheEntry {
    private final InformacionAlCiudadano value;
    private final long timestamp;

    public CacheEntry(InformacionAlCiudadano value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    
    public InformacionAlCiudadano getValue() {
        return value;
    }

    
    public long getTimestamp() {
        return timestamp;
    }
}