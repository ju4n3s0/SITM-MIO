package com.sitm.mio.datacenter.interfaces;

import com.sitm.mio.datacenter.ice.DataCenterEventPublisherI;

/**
 * Interface for main DataCenter controller.
 * Realized by: Controlador
 */
public interface IController {
    void start();
    void stop();
    void processDatagram(Object datagram);
    Object handleQuery(String queryType, Object params);
    void setIcePublisher(DataCenterEventPublisherI icePublisher);
}
