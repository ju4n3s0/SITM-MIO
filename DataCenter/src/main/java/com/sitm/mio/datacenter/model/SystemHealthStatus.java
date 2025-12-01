package com.sitm.mio.datacenter.model;


//DTO representing overall health of the DataCenter.

public class SystemHealthStatus {

    private final boolean databaseUp;
    private final boolean datagramReceiverRunning;
    private final boolean eventBusConfigured;
    private final boolean healthy;
    private final String message;

    public SystemHealthStatus(boolean databaseUp,
                            boolean datagramReceiverRunning,
                            boolean eventBusConfigured,
                            String message) {
        this.databaseUp = databaseUp;
        this.datagramReceiverRunning = datagramReceiverRunning;
        this.eventBusConfigured = eventBusConfigured;
        this.healthy = databaseUp && datagramReceiverRunning && eventBusConfigured;
        this.message = message;
    }

    public boolean isDatabaseUp() { return databaseUp; }
    public boolean isDatagramReceiverRunning() { return datagramReceiverRunning; }
    public boolean isEventBusConfigured() { return eventBusConfigured; }
    public boolean isHealthy() { return healthy; }
    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "SystemHealthStatus{" +
                "databaseUp=" + databaseUp +
                ", datagramReceiverRunning=" + datagramReceiverRunning +
                ", eventBusConfigured=" + eventBusConfigured +
                ", healthy=" + healthy +
                ", message='" + message + '\'' +
                '}';
    }
}