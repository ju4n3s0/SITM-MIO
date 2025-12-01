package com.sitm.mio.proxyserver.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import SITM.CitizenInformation;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles cache persistence to/from JSON files.
 * Provides atomic save operations and TTL validation on load.
 */
public class CachePersistence {
    
    private static final String CACHE_VERSION = "1.0";
    private static final String DEFAULT_CACHE_FILE = "cache/proxyserver-cache.json";
    
    private final Gson gson;
    private final Path cacheFilePath;
    private final long ttlMillis;
    
    /**
     * Create cache persistence manager with default file location.
     * @param ttlMillis Time-to-live in milliseconds for cache validation
     */
    public CachePersistence(long ttlMillis) {
        this(DEFAULT_CACHE_FILE, ttlMillis);
    }
    
    /**
     * Create cache persistence manager with custom file location.
     * @param cacheFilePath Path to cache file
     * @param ttlMillis Time-to-live in milliseconds for cache validation
     */
    public CachePersistence(String cacheFilePath, long ttlMillis) {
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
        this.cacheFilePath = Paths.get(cacheFilePath);
        this.ttlMillis = ttlMillis;
        
        // Ensure cache directory exists
        try {
            Files.createDirectories(this.cacheFilePath.getParent());
        } catch (IOException e) {
            System.err.println("Warning: Could not create cache directory: " + e.getMessage());
        }
    }
    
    /**
     * Save cache to JSON file atomically.
     * Uses temp file + rename for atomic operation.
     * 
     * @param storage The cache storage to persist
     * @return true if save successful
     */
    public boolean save(Map<String, CacheEntry> storage) {
        try {
            // Convert CacheEntry map to DTO map
            Map<String, CacheEntryDTO> dtoMap = new HashMap<>();
            for (Map.Entry<String, CacheEntry> entry : storage.entrySet()) {
                CacheEntry cacheEntry = entry.getValue();
                
                // Serialize the value to JSON string
                String dataJson = gson.toJson(cacheEntry.getRawValue());
                
                CacheEntryDTO dto = new CacheEntryDTO(
                    cacheEntry.getType().name(),
                    dataJson,
                    cacheEntry.getTimestamp()
                );
                dtoMap.put(entry.getKey(), dto);
            }
            
            // Create root DTO
            CacheDataDTO cacheData = new CacheDataDTO(
                CACHE_VERSION,
                System.currentTimeMillis(),
                dtoMap
            );
            
            // Write to temp file first (atomic operation)
            Path tempFile = Paths.get(cacheFilePath.toString() + ".tmp");
            try (Writer writer = Files.newBufferedWriter(tempFile)) {
                gson.toJson(cacheData, writer);
            }
            
            // Atomic rename
            Files.move(tempFile, cacheFilePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            
            System.out.println("Cache persisted: " + dtoMap.size() + " entries saved to " + cacheFilePath);
            return true;
            
        } catch (IOException e) {
            System.err.println("Failed to save cache: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load cache from JSON file.
     * Validates TTL and skips expired entries.
     * 
     * @return Map of cache entries, or empty map if file doesn't exist or error
     */
    public Map<String, CacheEntry> load() {
        Map<String, CacheEntry> result = new ConcurrentHashMap<>();
        
        if (!Files.exists(cacheFilePath)) {
            System.out.println("No cache file found at " + cacheFilePath + ", starting with empty cache");
            return result;
        }
        
        try (Reader reader = Files.newBufferedReader(cacheFilePath)) {
            CacheDataDTO cacheData = gson.fromJson(reader, CacheDataDTO.class);
            
            if (cacheData == null || cacheData.getEntries() == null) {
                System.out.println("Cache file is empty or invalid");
                return result;
            }
            
            System.out.println("Loading cache from " + cacheFilePath);
            System.out.println("  Cache version: " + cacheData.getVersion());
            System.out.println("  Saved at: " + new java.util.Date(cacheData.getSavedAt()));
            System.out.println("  Total entries: " + cacheData.getEntries().size());
            
            long now = System.currentTimeMillis();
            int validCount = 0;
            int expiredCount = 0;
            
            // Convert DTOs back to CacheEntry objects
            for (Map.Entry<String, CacheEntryDTO> entry : cacheData.getEntries().entrySet()) {
                CacheEntryDTO dto = entry.getValue();
                
                // Check if entry is still valid (not expired)
                long age = now - dto.getTimestamp();
                if (age <= ttlMillis) {
                    try {
                        // Determine cache type
                        CacheType type = CacheType.valueOf(dto.getType());
                        
                        // Deserialize based on type
                        Object value;
                        switch (type) {
                            case CITIZEN:
                                value = gson.fromJson(dto.getData(), CitizenInformation.class);
                                break;
                            case SYSTEM_STATS:
                                value = gson.fromJson(dto.getData(), SITM.SystemStatistics.class);
                                break;
                            case ZONE_DATAGRAM:
                                value = gson.fromJson(dto.getData(), SITM.ZoneStatistics.class);
                                break;
                            case HISTORICAL:
                                value = gson.fromJson(dto.getData(), SITM.HistoricalData.class);
                                break;
                            default:
                                System.err.println("Unknown cache type: " + dto.getType());
                                continue;
                        }
                        
                        // Create CacheEntry with type
                        CacheEntry cacheEntry = new CacheEntry(value, dto.getTimestamp(), type);
                        result.put(entry.getKey(), cacheEntry);
                        validCount++;
                    } catch (Exception e) {
                        System.err.println("Error deserializing entry: " + e.getMessage());
                        expiredCount++;
                    }
                } else {
                    expiredCount++;
                }
            }
            
            System.out.println("  Valid entries loaded: " + validCount);
            if (expiredCount > 0) {
                System.out.println("  Expired entries skipped: " + expiredCount);
            }
            
            return result;
            
        } catch (IOException e) {
            System.err.println("Failed to load cache: " + e.getMessage());
            return result;
        } catch (Exception e) {
            System.err.println("Error parsing cache file: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * Delete the cache file.
     * @return true if deleted successfully
     */
    public boolean clear() {
        try {
            if (Files.exists(cacheFilePath)) {
                Files.delete(cacheFilePath);
                System.out.println("Cache file deleted: " + cacheFilePath);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("Failed to delete cache file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get the cache file path.
     */
    public Path getCacheFilePath() {
        return cacheFilePath;
    }
}
