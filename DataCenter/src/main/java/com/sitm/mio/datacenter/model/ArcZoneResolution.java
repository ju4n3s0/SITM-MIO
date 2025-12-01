package com.sitm.mio.datacenter.model;

public class ArcZoneResolution {
    //This one doesn't have the part of the distancemeters cuz i think that is
    //for the op.control
    private final Long arcId;
    private final String zoneId;
    private final double latitude;
    private final double longitude;
    private final Double distanceMeters; 

    public ArcZoneResolution(Long arcId,
                            String zoneId,
                            double latitude,
                            double longitude,
                            Double distanceMeters
                            ) {
        this.arcId = arcId;
        this.zoneId = zoneId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceMeters = distanceMeters;
    }

    public Long getArcId() {
        return arcId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Double getDistanceMeters() {
        return distanceMeters;
    }

    @Override
    public String toString() {
        return "ArcZoneResolution{" +
                "arcId=" + arcId +
                ", zoneId='" + zoneId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", distanceMeters=" + distanceMeters +
                '}';
    }
}