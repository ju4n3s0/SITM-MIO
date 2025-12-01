package com.sitm.mio.simulator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration loader for Bus Simulator.
 */
public class ConfigLoader {
    
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties;
    
    static {
        loadConfiguration();
    }
    
    private static void loadConfiguration() {
        properties = new Properties();
        
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            System.out.println("[ConfigLoader] Configuration loaded from " + CONFIG_FILE);
        } catch (IOException e) {
            try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
                if (input != null) {
                    properties.load(input);
                    System.out.println("[ConfigLoader] Configuration loaded from classpath");
                } else {
                    System.err.println("[ConfigLoader] config.properties not found, using defaults");
                    setDefaults();
                }
            } catch (IOException ex) {
                System.err.println("[ConfigLoader] Error loading configuration: " + ex.getMessage());
                setDefaults();
            }
        }
    }
    
    private static void setDefaults() {
        properties.setProperty("database.url", "jdbc:postgresql://localhost:5432/SITM-MIOJM");
        properties.setProperty("database.user", "postgres");
        properties.setProperty("database.password", "postgres");
        properties.setProperty("simulation.bus.count", "5");
        properties.setProperty("simulation.interval.ms", "2000");
        properties.setProperty("simulation.speed.min", "10");
        properties.setProperty("simulation.speed.max", "60");
        properties.setProperty("gps.latitude.min", "3.35");
        properties.setProperty("gps.latitude.max", "3.50");
        properties.setProperty("gps.longitude.min", "-76.55");
        properties.setProperty("gps.longitude.max", "-76.50");
        properties.setProperty("bus.lines", "1,2,3");
    }
    
    public static String getDatabaseUrl() {
        return properties.getProperty("database.url");
    }
    
    public static String getDatabaseUser() {
        return properties.getProperty("database.user");
    }
    
    public static String getDatabasePassword() {
        return properties.getProperty("database.password");
    }
    
    public static int getBusCount() {
        return Integer.parseInt(properties.getProperty("simulation.bus.count", "5"));
    }
    
    public static long getIntervalMs() {
        return Long.parseLong(properties.getProperty("simulation.interval.ms", "2000"));
    }
    
    public static int getSpeedMin() {
        return Integer.parseInt(properties.getProperty("simulation.speed.min", "10"));
    }
    
    public static int getSpeedMax() {
        return Integer.parseInt(properties.getProperty("simulation.speed.max", "60"));
    }
    
    public static double getLatitudeMin() {
        return Double.parseDouble(properties.getProperty("gps.latitude.min", "3.35"));
    }
    
    public static double getLatitudeMax() {
        return Double.parseDouble(properties.getProperty("gps.latitude.max", "3.50"));
    }
    
    public static double getLongitudeMin() {
        return Double.parseDouble(properties.getProperty("gps.longitude.min", "-76.55"));
    }
    
    public static double getLongitudeMax() {
        return Double.parseDouble(properties.getProperty("gps.longitude.max", "-76.50"));
    }
    
    public static int[] getBusLines() {
        String linesStr = properties.getProperty("bus.lines", "1,2,3");
        String[] parts = linesStr.split(",");
        int[] lines = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            lines[i] = Integer.parseInt(parts[i].trim());
        }
        return lines;
    }
    
    public static String getSimulationMode() {
        return properties.getProperty("simulation.mode", "DATABASE").toUpperCase();
    }
    
    public static String getDataCenterHost() {
        return properties.getProperty("datacenter.host", "localhost");
    }
    
    public static int getDataCenterPort() {
        return Integer.parseInt(properties.getProperty("datacenter.port", "5000"));
    }
}
