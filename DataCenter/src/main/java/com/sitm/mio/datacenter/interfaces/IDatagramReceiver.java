package com.sitm.mio.datacenter.interfaces;

/**
 * Interface for datagram reception from buses.
 * Receives bus telemetry data and publishes events to the event bus.
 * 
 * Realized by: DatagramReceiver
 */
public interface IDatagramReceiver {
    void start();
    void stop();
    boolean isRunning();
}
