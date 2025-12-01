package com.sitm.mio.observer.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration loader for Observer module.
 * Loads configuration from config.properties file or system properties.
 * 
 * Priority order:
 * 1. System properties (-D flags)
 * 2. config.properties file
 * 3. Default values
 */
public class ConfigLoader {
    
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();
    private static boolean loaded = false;
    
    // Default values
    private static final String DEFAULT_WEBSOCKET_URL = "ws://localhost:8080/stream/observer";
    private static final String DEFAULT_API_PORT = "8081";
    private static final String DEFAULT_PROXY_URL = "http://localhost:8080";
    
    /**
     * Load configuration from file and system properties.
     */
    public static synchronized void load() {
        if (loaded) {
            return;
        }
        
        // Try to load from config.properties file
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            System.out.println("Configuration loaded from " + CONFIG_FILE);
        } catch (IOException e) {
            System.out.println("Config file not found, using defaults and system properties");
        }
        
        // Try to load from classpath (if packaged in JAR)
        if (properties.isEmpty()) {
            try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
                if (input != null) {
                    properties.load(input);
                    System.out.println("Configuration loaded from classpath");
                }
            } catch (IOException e) {
                System.out.println("Could not load config from classpath");
            }
        }
        
        loaded = true;
    }
    
    /**
     * Get WebSocket URL for event streaming.
     */
    public static String getWebSocketUrl() {
        load();
        return System.getProperty("ws.url",
            properties.getProperty("websocket.url", DEFAULT_WEBSOCKET_URL));
    }
    
    /**
     * Get API server port.
     */
    public static int getApiServerPort() {
        load();
        String port = System.getProperty("api.port",
            properties.getProperty("api.server.port", DEFAULT_API_PORT));
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            return 8081;
        }
    }
    
    /**
     * Get ProxyCache URL (optional).
     */
    public static String getProxyUrl() {
        load();
        return System.getProperty("proxy.url",
            properties.getProperty("proxy.url", DEFAULT_PROXY_URL));
    }
    
    /**
     * Get analytics update interval in milliseconds.
     */
    public static int getAnalyticsUpdateInterval() {
        load();
        String interval = properties.getProperty("analytics.update.interval", "5000");
        try {
            return Integer.parseInt(interval);
        } catch (NumberFormatException e) {
            return 5000;
        }
    }
    
    /**
     * Get positions buffer size.
     */
    public static int getPositionsBufferSize() {
        load();
        String size = properties.getProperty("analytics.positions.buffer.size", "1000");
        try {
            return Integer.parseInt(size);
        } catch (NumberFormatException e) {
            return 1000;
        }
    }
    
    /**
     * Get zone stats buffer size.
     */
    public static int getZoneStatsBufferSize() {
        load();
        String size = properties.getProperty("analytics.zonestats.buffer.size", "500");
        try {
            return Integer.parseInt(size);
        } catch (NumberFormatException e) {
            return 500;
        }
    }
    
    /**
     * Print current configuration.
     */
    public static void printConfiguration() {
        load();
        System.out.println("\n========== Observer Configuration ==========");
        System.out.println("WebSocket URL:         " + getWebSocketUrl());
        System.out.println("API Server Port:       " + getApiServerPort());
        System.out.println("ProxyCache URL:        " + getProxyUrl());
        System.out.println("Update Interval:       " + getAnalyticsUpdateInterval() + "ms");
        System.out.println("Positions Buffer:      " + getPositionsBufferSize());
        System.out.println("Zone Stats Buffer:     " + getZoneStatsBufferSize());
        System.out.println("============================================\n");
    }
    
    /**
     * Reload configuration (useful for testing).
     */
    public static synchronized void reload() {
        loaded = false;
        properties.clear();
        load();
    }
}
