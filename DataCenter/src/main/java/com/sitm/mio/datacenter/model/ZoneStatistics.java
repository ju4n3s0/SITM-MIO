package com.sitm.mio.datacenter.model;


//DTO with statistics for a specific zone.
public class ZoneStatistics {

    private final String zoneId;
    private final int stopCount;

    public ZoneStatistics(String zoneId, int stopCount) {
        this.zoneId = zoneId;
        this.stopCount = stopCount;
    }

    public String getZoneId() {
        return zoneId;
    }

    public int getStopCount() {
        return stopCount;
    }

    @Override
    public String toString() {
        return "ZoneStatistics{" +
                "zoneId='" + zoneId + '\'' +
                ", stopCount=" + stopCount +
                '}';
    }
}