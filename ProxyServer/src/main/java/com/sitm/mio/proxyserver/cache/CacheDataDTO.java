package com.sitm.mio.proxyserver.cache;

import java.util.Map;

/**
 * Root DTO for cache persistence.
 * Contains metadata and all cache entries.
 */
public class CacheDataDTO {
    private String version;
    private long savedAt;
    private Map<String, CacheEntryDTO> entries;
    
    // Default constructor for Gson
    public CacheDataDTO() {
    }
    
    public CacheDataDTO(String version, long savedAt, Map<String, CacheEntryDTO> entries) {
        this.version = version;
        this.savedAt = savedAt;
        this.entries = entries;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public long getSavedAt() {
        return savedAt;
    }
    
    public void setSavedAt(long savedAt) {
        this.savedAt = savedAt;
    }
    
    public Map<String, CacheEntryDTO> getEntries() {
        return entries;
    }
    
    public void setEntries(Map<String, CacheEntryDTO> entries) {
        this.entries = entries;
    }
}
