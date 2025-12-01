package com.sitm.mio.proxyserver.cache;

/**
 * Data Transfer Object for cache entries during JSON serialization.
 * Stores cache entry data with type information and JSON-serialized value.
 */
public class CacheEntryDTO {
    private long timestamp;
    private String type;      // CacheType as string
    private String data;      // JSON-serialized value
    
    // Default constructor for Gson
    public CacheEntryDTO() {
    }
    
    public CacheEntryDTO(long timestamp, String type, String data) {
        this.timestamp = timestamp;
        this.type = type;
        this.data = data;
    }
    
    public CacheEntryDTO(String type, String data, long timestamp) {
        this.type = type;
        this.data = data;
        this.timestamp = timestamp;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
}
