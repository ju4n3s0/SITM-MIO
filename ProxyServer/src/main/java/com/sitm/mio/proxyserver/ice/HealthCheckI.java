package com.sitm.mio.proxyserver.ice;

import com.zeroc.Ice.Current;
import SITM.HealthCheck;

/**
 * ICE servant implementation for HealthCheck interface.
 * Provides simple health check functionality.
 */
public class HealthCheckI implements HealthCheck {
    
    private final long startTime;
    
    public HealthCheckI() {
        this.startTime = System.currentTimeMillis();
        System.out.println("HealthCheckI servant created");
    }
    
    @Override
    public boolean ping(Current current) {
        return true;
    }
    
    @Override
    public String getStatus(Current current) {
        long uptime = System.currentTimeMillis() - startTime;
        long uptimeSeconds = uptime / 1000;
        
        return String.format("ProxyServer is UP (uptime: %d seconds)", uptimeSeconds);
    }
}
