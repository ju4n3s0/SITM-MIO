package com.sitm.mio.datacenter.component;

import com.sitm.mio.datacenter.interfaces.IZoneVisualizationManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages zone visualization and boundaries.
 * Component from deployment diagram: GestorVisualizaciónZonas
 * 
 * Realizes: IZoneVisualizationManager
 * 
 * Zone Grid: 4 rows x 10 columns
 * Covers Cali, Colombia area:
 * - Latitude: 3.29077 to 3.49886333
 * - Longitude: -76.58889111 to -76.46328111
 */
public class ZoneVisualizationManager implements IZoneVisualizationManager {
    
    // Zone grid constants (from ZoneUtils)
    private static final double MIN_LAT = 3.29077;
    private static final double MAX_LAT = 3.49886333;
    private static final double MIN_LON = -76.58889111;
    private static final double MAX_LON = -76.46328111;
    private static final int ROWS = 4;
    private static final int COLUMNS = 10;
    
    @Override
    public Object getZoneBoundaries(String zoneId) {
        System.out.println("[ZoneVisualizationManager] Getting boundaries for zone: " + zoneId);
        
        // Parse zone ID (format: "Z{row}_{col}")
        if (zoneId == null || !zoneId.matches("Z\\d+_\\d+")) {
            System.err.println("[ZoneVisualizationManager] Invalid zone ID: " + zoneId);
            return null;
        }
        
        String[] parts = zoneId.substring(1).split("_");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        
        if (row < 0 || row >= ROWS || col < 0 || col >= COLUMNS) {
            System.err.println("[ZoneVisualizationManager] Zone out of bounds: " + zoneId);
            return null;
        }
        
        // Calculate zone boundaries
        double latStep = (MAX_LAT - MIN_LAT) / ROWS;
        double lonStep = (MAX_LON - MIN_LON) / COLUMNS;
        
        double minLat = MIN_LAT + (row * latStep);
        double maxLat = MIN_LAT + ((row + 1) * latStep);
        double minLon = MIN_LON + (col * lonStep);
        double maxLon = MIN_LON + ((col + 1) * lonStep);
        
        // Create boundary object
        Map<String, Object> boundaries = new HashMap<>();
        boundaries.put("zoneId", zoneId);
        boundaries.put("minLatitude", minLat);
        boundaries.put("maxLatitude", maxLat);
        boundaries.put("minLongitude", minLon);
        boundaries.put("maxLongitude", maxLon);
        boundaries.put("centerLatitude", (minLat + maxLat) / 2);
        boundaries.put("centerLongitude", (minLon + maxLon) / 2);
        
        // Add corner coordinates for polygon drawing
        List<Map<String, Double>> corners = new ArrayList<>();
        corners.add(Map.of("lat", minLat, "lon", minLon)); // Southwest
        corners.add(Map.of("lat", minLat, "lon", maxLon)); // Southeast
        corners.add(Map.of("lat", maxLat, "lon", maxLon)); // Northeast
        corners.add(Map.of("lat", maxLat, "lon", minLon)); // Northwest
        boundaries.put("corners", corners);
        
        System.out.println("[ZoneVisualizationManager] Zone " + zoneId + ": " +
                          String.format("[%.6f, %.6f] to [%.6f, %.6f]", minLat, minLon, maxLat, maxLon));
        
        return boundaries;
    }
    
    @Override
    public List<Object> getAllZones() {
        System.out.println("[ZoneVisualizationManager] Getting all zones (" + (ROWS * COLUMNS) + " total)");
        
        List<Object> allZones = new ArrayList<>();
        
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                String zoneId = "Z" + row + "_" + col;
                Object boundaries = getZoneBoundaries(zoneId);
                if (boundaries != null) {
                    allZones.add(boundaries);
                }
            }
        }
        
        System.out.println("[ZoneVisualizationManager] Returned " + allZones.size() + " zones");
        return allZones;
    }
    
    @Override
    public Object getZoneMetadata(String zoneId) {
        System.out.println("[ZoneVisualizationManager] Getting metadata for zone: " + zoneId);
        
        // Parse zone ID
        if (zoneId == null || !zoneId.matches("Z\\d+_\\d+")) {
            return null;
        }
        
        String[] parts = zoneId.substring(1).split("_");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        
        // Create metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("zoneId", zoneId);
        metadata.put("row", row);
        metadata.put("column", col);
        metadata.put("gridSize", ROWS + "x" + COLUMNS);
        
        // Add descriptive name based on position
        String latDesc = row == 0 ? "South" : row == 1 ? "South-Central" : row == 2 ? "North-Central" : "North";
        String lonDesc = col < 3 ? "West" : col < 7 ? "Central" : "East";
        metadata.put("description", latDesc + " " + lonDesc);
        
        // Add neighboring zones
        List<String> neighbors = new ArrayList<>();
        if (row > 0) neighbors.add("Z" + (row - 1) + "_" + col); // South
        if (row < ROWS - 1) neighbors.add("Z" + (row + 1) + "_" + col); // North
        if (col > 0) neighbors.add("Z" + row + "_" + (col - 1)); // West
        if (col < COLUMNS - 1) neighbors.add("Z" + row + "_" + (col + 1)); // East
        metadata.put("neighbors", neighbors);
        
        return metadata;
    }
}
