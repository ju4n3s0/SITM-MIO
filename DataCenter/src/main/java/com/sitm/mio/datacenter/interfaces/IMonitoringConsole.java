package com.sitm.mio.datacenter.interfaces;

/**
 * Interface for system monitoring and diagnostics.
 * Realized by: ConsolaDeMonitoreo
 */
public interface IMonitoringConsole {
    Object getSystemHealth();
    Object getMetrics();
    void logEvent(String eventType, Object data);
    boolean isHealthy();
}
