package com.sitm.mio.citizen.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration loader for Citizen module.
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
        properties.setProperty("proxyserver.url", "http://localhost:8080");
        properties.setProperty("proxyserver.timeout.seconds", "30");
        properties.setProperty("proxyserver.retry.attempts", "3");
        properties.setProperty("cache.enabled", "true");
        properties.setProperty("cache.max.entries", "100");
    }
    
    /**
     * Get ProxyServer URL.
     * @return ProxyServer URL
     */
    public static String getProxyServerUrl() {
        return System.getProperty("proxyserver.url", "http://localhost:8080");
    }
    
    public static String getProxyServerHost() {
        return System.getProperty("proxyserver.host", "localhost");
    }
    
    public static int getProxyServerPort() {
        return Integer.parseInt(System.getProperty("proxyserver.port", "10000"));
    }
    
    /**
     * Get ProxyServer connection timeout in seconds.
     * @return Timeout in seconds
     */
    public static int getProxyServerTimeout() {
        return Integer.parseInt(properties.getProperty("proxyserver.timeout.seconds", "30"));
    }
    
    /**
     * Get number of retry attempts for ProxyServer connection.
     * @return Number of retry attempts
     */
    public static int getRetryAttempts() {
        return Integer.parseInt(properties.getProperty("proxyserver.retry.attempts", "3"));
    }
    
    /**
     * Check if local cache is enabled.
     * @return true if cache is enabled
     */
    public static boolean isCacheEnabled() {
        return Boolean.parseBoolean(properties.getProperty("cache.enabled", "true"));
    }
    
    /**
     * Get maximum number of cache entries.
     * @return Maximum cache entries
     */
    public static int getMaxCacheEntries() {
        return Integer.parseInt(properties.getProperty("cache.max.entries", "100"));
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
        System.out.println("=== Citizen Configuration ===");
        System.out.println("ProxyServer URL: " + getProxyServerUrl());
        System.out.println("Connection Timeout: " + getProxyServerTimeout() + "s");
        System.out.println("Retry Attempts: " + getRetryAttempts());
        System.out.println("Cache Enabled: " + isCacheEnabled());
        System.out.println("Max Cache Entries: " + getMaxCacheEntries());
        System.out.println("============================");
    }
}
