package com.sitm.mio.datacenter.interfaces;

/**
 * Interface for main DataCenter controller.
 * Realized by: Controlador
 */
public interface IController {
    void start();
    void stop();
    void processDatagram(Object datagram);
    Object handleQuery(String queryType, Object params);
}
