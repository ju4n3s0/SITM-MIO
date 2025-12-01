package com.sitm.mio.datacenter.component;

import java.sql.Connection;

import com.sitm.mio.datacenter.config.ManageDatabase;
import com.sitm.mio.datacenter.interfaces.IDatagramReceiver;
import com.sitm.mio.datacenter.interfaces.IEventBus;
import com.sitm.mio.datacenter.interfaces.IMonitoringConsole;
import com.sitm.mio.datacenter.model.SystemHealthStatus;
import com.sitm.mio.datacenter.model.SystemMetrics;

/**
 * System monitoring and diagnostics console.
 * Component from deployment diagram: ConsolaDeMonitoreo
 * 
 * Realizes: IMonitoringConsole
 */
public class MonitoringConsole implements IMonitoringConsole {
    private final IDatagramReceiver datagramReceiver;
    private final IEventBus eventBus;
    
    private long eventsLogged = 0;

    public MonitoringConsole(IDatagramReceiver datagramReceiver,
                            IEventBus eventBus) {
        this.datagramReceiver = datagramReceiver;
        this.eventBus = eventBus;
    }


    @Override
    public Object getSystemHealth() {
        boolean dbUp = checkDatabase();
        boolean udpRunning = (datagramReceiver != null) && datagramReceiver.isRunning();
        boolean busConfigured = (eventBus != null);

        String msg = "DB=" + dbUp +
                    ", UDPReceiverRunning=" + udpRunning +
                    ", EventBusConfigured=" + busConfigured;

        SystemHealthStatus status =
                new SystemHealthStatus(dbUp, udpRunning, busConfigured, msg);

        System.out.println("[MonitoringConsole] System health: " + status);
        return status;

    }
    
    @Override
    public Object getMetrics() {
        SystemMetrics metrics =
                new SystemMetrics(eventsLogged, System.currentTimeMillis());
        System.out.println("[MonitoringConsole] Metrics: " + metrics);
        return metrics;
    }
    
    @Override
    public void logEvent(String eventType, Object data) {
        eventsLogged++;
        System.out.println("[MonitoringConsole] Logging event [" + eventType + "]: " + data);
    }
    
    @Override
    public boolean isHealthy() {
        SystemHealthStatus status = (SystemHealthStatus) getSystemHealth();
        return status.isHealthy();
    }

    private boolean checkDatabase() {
        try (Connection con = ManageDatabase.gConnection()) {
            return con != null && !con.isClosed();
        } catch (Exception e) {
            System.err.println("[MonitoringConsole] Database health check failed: " + e.getMessage());
            return false;
        }
    }
}
