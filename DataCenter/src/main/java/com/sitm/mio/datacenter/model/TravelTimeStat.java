package com.sitm.mio.datacenter.model;

import java.time.Instant;

public class TravelTimeStat {

    private final String zoneId;
    private final long originStopId;
    private final long destinationStopId;
    private final double avgTimeMinutes;
    private final int sampleCount;
    private final Instant lastUpdated;

    public TravelTimeStat(String zoneId,
                        long originStopId,
                        long destinationStopId,
                        double avgTimeMinutes,
                        int sampleCount,
                        Instant lastUpdated) {
        this.zoneId = zoneId;
        this.originStopId = originStopId;
        this.destinationStopId = destinationStopId;
        this.avgTimeMinutes = avgTimeMinutes;
        this.sampleCount = sampleCount;
        this.lastUpdated = lastUpdated;
    }

    public String getZoneId() {
        return zoneId;
    }

    public long getOriginStopId() {
        return originStopId;
    }

    public long getDestinationStopId() {
        return destinationStopId;
    }

    public double getAvgTimeMinutes() {
        return avgTimeMinutes;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }
}