package com.sitm.mio.datacenter.interfaces;

/**
 * Interface for UDP datagram reception from buses.
 * Realized by: ReceptorDatagramas
 */
public interface IDatagramReceiver {
    void start();
    void stop();
    boolean isRunning();
}
