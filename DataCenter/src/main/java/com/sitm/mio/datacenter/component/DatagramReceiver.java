package com.sitm.mio.datacenter.component;

import com.sitm.mio.datacenter.interfaces.IDatagramReceiver;
import com.sitm.mio.datacenter.interfaces.IEventBus;

/**
 * UDP datagram receiver for bus telemetry.
 * Component from deployment diagram: ReceptorDatagramas
 * 
 * Realizes: IDatagramReceiver
 * Uses: IEventBus
 */
public class DatagramReceiver implements IDatagramReceiver {
    
    private final IEventBus eventBus;
    private boolean running = false;
    
    public DatagramReceiver(IEventBus eventBus) {
        this.eventBus = eventBus;
    }
    
    @Override
    public void start() {
        // TODO: Start UDP socket listener
        // 1. Create DatagramSocket on configured port
        // 2. Start listener thread
        // 3. Parse incoming datagrams
        // 4. Publish EventoNuevoDatagrama to eventBus
        this.running = true;
        System.out.println("DatagramReceiver started - listening for UDP datagrams");
    }
    
    @Override
    public void stop() {
        // TODO: Stop UDP socket
        this.running = false;
        System.out.println("DatagramReceiver stopped");
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
}
