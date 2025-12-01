package com.sitm.mio.datacenter.model;

//This is a dto with a global sistem statistics for the Datacenter
public class SystemStatistics {

    private final int totalStops;
    private final int totalLines;

    public SystemStatistics(int totalStops, int totalLines) {
        this.totalStops = totalStops;
        this.totalLines = totalLines;
    }

    public int getTotalStops() {
        return totalStops;
    }

    public int getTotalLines() {
        return totalLines;
    }

    @Override
    public String toString() {
        return "SystemStatistics{" +
                "totalStops=" + totalStops +
                ", totalLines=" + totalLines +
                '}';
    }
}