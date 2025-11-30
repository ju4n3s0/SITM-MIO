package com.sitm.mio.proxyserver.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration loader for ProxyServer module.
 * Loads settings from config.properties file.
 */
public class ConfigLoader {
    
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties;
    
    static {
        loadConfiguration();
    }
    
    /**
     * Load configuration from config.properties file.
     */
    private static void loadConfiguration() {
        properties = new Properties();
        
        // Try to load from file system first
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            System.out.println("Configuration loaded from " + CONFIG_FILE);
        } catch (IOException e) {
            // Try to load from classpath
            try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
                if (input != null) {
                    properties.load(input);
                    System.out.println("Configuration loaded from classpath");
                } else {
                    System.err.println("Warning: config.properties not found, using defaults");
                    setDefaults();
                }
            } catch (IOException ex) {
                System.err.println("Error loading configuration: " + ex.getMessage());
                setDefaults();
            }
        }
    }
    
    /**
     * Set default configuration values.
     */
    private static void setDefaults() {
        properties.setProperty("server.port", "8080");
        properties.setProperty("server.host", "0.0.0.0");
        properties.setProperty("datacenter.url", "http://localhost:9090");
        properties.setProperty("datacenter.timeout.seconds", "30");
        properties.setProperty("cache.ttl.minutes", "10");
        properties.setProperty("cache.max.entries", "1000");
    }
    
    /**
     * Get server port.
     * @return Server port
     */
    public static int getServerPort() {
        return Integer.parseInt(properties.getProperty("server.port", "8080"));
    }
    
    /**
     * Get server host.
     * @return Server host
     */
    public static String getServerHost() {
        return properties.getProperty("server.host", "0.0.0.0");
    }
    
    /**
     * Get DataCenter URL.
     * @return DataCenter URL
     */
    public static String getDataCenterUrl() {
        return properties.getProperty("datacenter.url", "http://localhost:9090");
    }
    
    /**
     * Get DataCenter connection timeout in seconds.
     * @return Timeout in seconds
     */
    public static int getDataCenterTimeout() {
        return Integer.parseInt(properties.getProperty("datacenter.timeout.seconds", "30"));
    }
    
    /**
     * Get cache TTL in minutes.
     * @return Cache TTL in minutes
     */
    public static int getCacheTTLMinutes() {
        return Integer.parseInt(properties.getProperty("cache.ttl.minutes", "10"));
    }
    
    /**
     * Get cache TTL in milliseconds.
     * @return Cache TTL in milliseconds
     */
    public static long getCacheTTLMillis() {
        return getCacheTTLMinutes() * 60 * 1000L;
    }
    
    /**
     * Get maximum number of cache entries.
     * @return Maximum cache entries
     */
    public static int getMaxCacheEntries() {
        return Integer.parseInt(properties.getProperty("cache.max.entries", "1000"));
    }
    
    /**
     * Check if cache is enabled.
     * @return true if cache is enabled
     */
    public static boolean isCacheEnabled() {
        return Boolean.parseBoolean(properties.getProperty("cache.enabled", "true"));
    }
    
    /**
     * Get a property value.
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Print all configuration values.
     */
    public static void printConfiguration() {
        System.out.println("=== ProxyServer Configuration ===");
        System.out.println("Server: " + getServerHost() + ":" + getServerPort());
        System.out.println("DataCenter URL: " + getDataCenterUrl());
        System.out.println("DataCenter Timeout: " + getDataCenterTimeout() + "s");
        System.out.println("Cache TTL: " + getCacheTTLMinutes() + " minutes");
        System.out.println("Cache Enabled: " + isCacheEnabled());
        System.out.println("Max Cache Entries: " + getMaxCacheEntries());
        System.out.println("================================");
    }
}
