package com.sitm.mio.datacenter.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration loader for DataCenter module.
 * Loads settings from config.properties and config.datacenter files.
 */
public class ConfigLoader {
    
    private static final String CONFIG_FILE = "config.properties";
    private static final String ICE_CONFIG_FILE = "config.datacenter";
    private static Properties properties;
    private static Properties iceProperties;
    
    static {
        loadConfiguration();
        loadIceConfiguration();
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
     * Load ICE configuration from config.datacenter file.
     */
    private static void loadIceConfiguration() {
        iceProperties = new Properties();
        
        // Try to load from file system first
        try (InputStream input = new FileInputStream(ICE_CONFIG_FILE)) {
            iceProperties.load(input);
            System.out.println("ICE Configuration loaded from " + ICE_CONFIG_FILE);
        } catch (IOException e) {
            // Try to load from classpath
            try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(ICE_CONFIG_FILE)) {
                if (input != null) {
                    iceProperties.load(input);
                    System.out.println("ICE Configuration loaded from classpath");
                } else {
                    System.err.println("Warning: " + ICE_CONFIG_FILE + " not found, using ICE defaults");
                    setIceDefaults();
                }
            } catch (IOException ex) {
                System.err.println("Error loading ICE configuration: " + ex.getMessage());
                setIceDefaults();
            }
        }
    }
    
    /**
     * Set default configuration values.
     */
    private static void setDefaults() {
        properties.setProperty("server.port", "9090");
        properties.setProperty("server.host", "0.0.0.0");
        properties.setProperty("database.url", "jdbc:postgresql://localhost:5432/mio");
        properties.setProperty("database.user", "postgres");
        properties.setProperty("database.password", "postgres");
        properties.setProperty("udp.receiver.port", "5000");
    }
    
    /**
     * Set default ICE configuration values.
     */
    private static void setIceDefaults() {
        iceProperties.setProperty("DataCenterAdapter.Endpoints", "tcp -h 0.0.0.0 -p 10003");
        iceProperties.setProperty("Ice.ThreadPool.Server.Size", "4");
        iceProperties.setProperty("Ice.ThreadPool.Server.SizeMax", "10");
        iceProperties.setProperty("Ice.Warn.Connections", "1");
        iceProperties.setProperty("Ice.Trace.Network", "0");
        iceProperties.setProperty("Ice.ACM.Server.Timeout", "30");
        iceProperties.setProperty("Ice.ACM.Server.Heartbeat", "3");
    }
    
    /**
     * Get server port.
     * @return Server port
     */
    public static int getServerPort() {
        return Integer.parseInt(properties.getProperty("server.port", "9090"));
    }
    
    /**
     * Get server host.
     * @return Server host
     */
    public static String getServerHost() {
        return properties.getProperty("server.host", "0.0.0.0");
    }
    
    /**
     * Get database URL.
     * @return Database URL
     */
    public static String getDatabaseUrl() {
        return properties.getProperty("database.url", "jdbc:postgresql://localhost:5432/mio");
    }
    
    /**
     * Get database user.
     * @return Database user
     */
    public static String getDatabaseUser() {
        return properties.getProperty("database.user", "postgres");
    }
    
    /**
     * Get database password.
     * @return Database password
     */
    public static String getDatabasePassword() {
        return properties.getProperty("database.password", "postgres");
    }
    
    /**
     * Get datagram source strategy.
     * @return "DATABASE" or "UDP"
     */
    public static String getDatagramSource() {
        return properties.getProperty("datagram.source", "DATABASE").toUpperCase();
    }
    
    /**
     * Get UDP receiver port.
     * @return UDP receiver port
     */
    public static int getUdpReceiverPort() {
        return Integer.parseInt(properties.getProperty("udp.receiver.port", "5000"));
    }
    
    /**
     * Get database poll interval in milliseconds.
     * @return Poll interval in ms
     */
    public static long getDatabasePollInterval() {
        return Long.parseLong(properties.getProperty("database.poll.interval", "10000"));
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
     * Get ICE property value.
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value
     */
    public static String getIceProperty(String key, String defaultValue) {
        return iceProperties.getProperty(key, defaultValue);
    }
    
    /**
     * Get all ICE properties for ICE initialization.
     * @return ICE properties
     */
    public static Properties getIceProperties() {
        return iceProperties;
    }
    
    /**
     * Get DataCenter adapter endpoints.
     * @return Adapter endpoints
     */
    public static String getDataCenterAdapterEndpoints() {
        return iceProperties.getProperty("DataCenterAdapter.Endpoints", "tcp -h 0.0.0.0 -p 10003");
    }
    
    /**
     * Get ICE server port.
     * @return ICE server port
     */
    public static int getIceServerPort() {
        String endpoints = getDataCenterAdapterEndpoints();
        // Extract port from "tcp -h 0.0.0.0 -p 10003"
        try {
            String[] parts = endpoints.split("-p");
            if (parts.length > 1) {
                return Integer.parseInt(parts[1].trim().split("\\s+")[0]);
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
        return 10003; // Default
    }
    
    /**
     * Print all configuration values.
     */
    public static void printConfiguration() {
        System.out.println("=== DataCenter Configuration ===");
        System.out.println("Server: " + getServerHost() + ":" + getServerPort());
        System.out.println("Database URL: " + getDatabaseUrl());
        System.out.println("Database User: " + getDatabaseUser());
        System.out.println("UDP Receiver Port: " + getUdpReceiverPort());
        System.out.println("================================");
        System.out.println("=== ICE Configuration ===");
        System.out.println("Adapter Endpoints: " + getDataCenterAdapterEndpoints());
        System.out.println("ICE Server Port: " + getIceServerPort());
        System.out.println("Thread Pool Size: " + iceProperties.getProperty("Ice.ThreadPool.Server.Size", "4"));
        System.out.println("========================");
    }
}
