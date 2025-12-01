package com.sitm.mio.datacenter.model;

import java.time.Instant;

public class TravelTimeStat {

    private final String zoneId;         // o lineId + zoneId, según tu diseño
    private final double avgTravelTime;  // en segundos o minutos
    private final Instant windowStart;   // inicio del período analizado
    private final Instant windowEnd;     // fin del período
    private final Instant createdAt;     // cuándo se guardó

    public TravelTimeStat(String zoneId,
                        double avgTravelTime,
                        Instant windowStart,
                        Instant windowEnd,
                        Instant createdAt) {
        this.zoneId = zoneId;
        this.avgTravelTime = avgTravelTime;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.createdAt = createdAt;
    }

    public String getZoneId() {
        return zoneId;
    }

    public double getAvgTravelTime() {
        return avgTravelTime;
    }

    public Instant getWindowStart() {
        return windowStart;
    }

    public Instant getWindowEnd() {
        return windowEnd;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }


}