package com.sitm.mio.datacenter.model;

import java.time.Instant;

public class EventNewDatagram {

    private final long datagramId;
    private final long busId;
    private final long lineId;
    private final double latitude;
    private final double longitude;
    private final Instant eventTimestamp;

    public EventNewDatagram(long datagramId,
                                long busId,
                                long lineId,
                                double latitude,
                                double longitude,
                                Instant eventTimestamp) {
        this.datagramId = datagramId;
        this.busId = busId;
        this.lineId = lineId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.eventTimestamp = eventTimestamp;
    }

    public long getDatagramId() {
        return datagramId;
    }

    public long getBusId() {
        return busId;
    }

    public long getLineId() {
        return lineId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Instant getEventTimestamp() {
        return eventTimestamp;
    }

    @Override
    public String toString() {
        return "EventoNuevoDatagrama{" +
                "datagramId=" + datagramId +
                ", busId=" + busId +
                ", lineId=" + lineId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", eventTimestamp=" + eventTimestamp +
                '}';
    }
}