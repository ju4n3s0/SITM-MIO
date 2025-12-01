package com.sitm.mio.datacenter.model;


// DTO with simple monitoring metrics.
// Can be extended later with more detailed metrics.

public class SystemMetrics {

    private final long eventsLogged;
    private final long lastUpdatedEpochMillis;

    public SystemMetrics(long eventsLogged, long lastUpdatedEpochMillis) {
        this.eventsLogged = eventsLogged;
        this.lastUpdatedEpochMillis = lastUpdatedEpochMillis;
    }

    public long getEventsLogged() { return eventsLogged; }
    public long getLastUpdatedEpochMillis() { return lastUpdatedEpochMillis; }

    @Override
    public String toString() {
        return "SystemMetrics{" +
                "eventsLogged=" + eventsLogged +
                ", lastUpdatedEpochMillis=" + lastUpdatedEpochMillis +
                '}';
    }
}