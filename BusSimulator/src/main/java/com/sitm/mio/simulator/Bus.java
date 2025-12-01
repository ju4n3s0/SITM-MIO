package com.sitm.mio.simulator;

import java.util.Random;

/**
 * Represents a simulated bus with position and movement.
 */
public class Bus {
    
    private final long busId;
    private final int lineId;
    private double latitude;
    private double longitude;
    private int speed; // km/h
    private final Random random;
    
    // GPS bounds
    private final double latMin;
    private final double latMax;
    private final double lonMin;
    private final double lonMax;
    
    public Bus(long busId, int lineId, double latMin, double latMax, double lonMin, double lonMax) {
        this.busId = busId;
        this.lineId = lineId;
        this.latMin = latMin;
        this.latMax = latMax;
        this.lonMin = lonMin;
        this.lonMax = lonMax;
        this.random = new Random();
        
        // Initialize random position within bounds
        this.latitude = latMin + (latMax - latMin) * random.nextDouble();
        this.longitude = lonMin + (lonMax - lonMin) * random.nextDouble();
        this.speed = ConfigLoader.getSpeedMin() + random.nextInt(ConfigLoader.getSpeedMax() - ConfigLoader.getSpeedMin());
    }
    
    /**
     * Update bus position based on current speed and direction.
     * Simulates movement with random walk.
     */
    public void updatePosition() {
        // Random walk: small changes in lat/lon
        // Speed affects how much the bus moves
        double movementFactor = speed / 100000.0; // Adjust for realistic movement
        
        // Random direction
        double latChange = (random.nextDouble() - 0.5) * movementFactor;
        double lonChange = (random.nextDouble() - 0.5) * movementFactor;
        
        // Update position
        latitude += latChange;
        longitude += lonChange;
        
        // Keep within bounds
        if (latitude < latMin) latitude = latMin;
        if (latitude > latMax) latitude = latMax;
        if (longitude < lonMin) longitude = lonMin;
        if (longitude > lonMax) longitude = lonMax;
        
        // Occasionally change speed
        if (random.nextDouble() < 0.1) { // 10% chance
            speed = ConfigLoader.getSpeedMin() + random.nextInt(ConfigLoader.getSpeedMax() - ConfigLoader.getSpeedMin());
        }
    }
    
    public long getBusId() {
        return busId;
    }
    
    public int getLineId() {
        return lineId;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public int getSpeed() {
        return speed;
    }
    
    /**
     * Get GPS X coordinate (longitude * 1e7 as integer).
     */
    public long getGpsX() {
        return (long) (longitude * 1e7);
    }
    
    /**
     * Get GPS Y coordinate (latitude * 1e7 as integer).
     */
    public long getGpsY() {
        return (long) (latitude * 1e7);
    }
    
    @Override
    public String toString() {
        return String.format("Bus[id=%d, line=%d, lat=%.6f, lon=%.6f, speed=%d km/h]",
                busId, lineId, latitude, longitude, speed);
    }
}
