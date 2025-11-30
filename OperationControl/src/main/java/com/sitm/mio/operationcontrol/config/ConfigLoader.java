package com.sitm.mio.operationcontrol.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration loader for OperationControl module.
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
    private static final String DEFAULT_PROXY_URL = "http://localhost:8080";
    private static final String DEFAULT_WEBSOCKET_URL = "ws://localhost:8080/stream/operator";
    private static final String DEFAULT_OBSERVER_URL = "http://localhost:8081";
    
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
     * Get ProxyCache server URL.
     * Priority: System property > config file > default
     */
    public static String getProxyUrl() {
        load();
        return System.getProperty("proxy.url",
            properties.getProperty("proxy.url", DEFAULT_PROXY_URL));
    }
    
    /**
     * Get WebSocket URL for event streaming.
     * Priority: System property > config file > default
     */
    public static String getWebSocketUrl() {
        load();
        return System.getProperty("websocket.url",
            properties.getProperty("websocket.url", DEFAULT_WEBSOCKET_URL));
    }
    
    /**
     * Get Observer analytics API URL.
     * Priority: System property > config file > default
     */
    public static String getObserverUrl() {
        load();
        return System.getProperty("observer.url",
            properties.getProperty("observer.url", DEFAULT_OBSERVER_URL));
    }
    
    /**
     * Get connection timeout in seconds.
     */
    public static int getConnectionTimeout() {
        load();
        String timeout = System.getProperty("connection.timeout",
            properties.getProperty("connection.timeout", "10"));
        try {
            return Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            return 10;
        }
    }
    
    /**
     * Get WebSocket reconnection attempts.
     */
    public static int getWebSocketReconnectAttempts() {
        load();
        String attempts = System.getProperty("websocket.reconnect.attempts",
            properties.getProperty("websocket.reconnect.attempts", "3"));
        try {
            return Integer.parseInt(attempts);
        } catch (NumberFormatException e) {
            return 3;
        }
    }
    
    /**
     * Get request retry count.
     */
    public static int getRequestRetryCount() {
        load();
        String retries = System.getProperty("request.retry.count",
            properties.getProperty("request.retry.count", "2"));
        try {
            return Integer.parseInt(retries);
        } catch (NumberFormatException e) {
            return 2;
        }
    }
    
    /**
     * Get a custom property value.
     */
    public static String getProperty(String key, String defaultValue) {
        load();
        return System.getProperty(key, properties.getProperty(key, defaultValue));
    }
    
    /**
     * Print current configuration.
     */
    public static void printConfiguration() {
        load();
        System.out.println("\n========== OperationControl Configuration ==========");
        System.out.println("ProxyCache URL:        " + getProxyUrl());
        System.out.println("WebSocket URL:         " + getWebSocketUrl());
        System.out.println("Observer URL:          " + getObserverUrl());
        System.out.println("Connection Timeout:    " + getConnectionTimeout() + "s");
        System.out.println("WS Reconnect Attempts: " + getWebSocketReconnectAttempts());
        System.out.println("Request Retry Count:   " + getRequestRetryCount());
        System.out.println("===================================================\n");
    }
    
    /**
     * Reload configuration
     */
    public static synchronized void reload() {
        loaded = false;
        properties.clear();
        load();
    }
}
