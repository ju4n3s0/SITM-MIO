package com.sitm.mio.datacenter.interfaces;

/**
 * Interface for sending data to citizen system.
 * Realized by: SenderCiudadanos
 */
public interface ICitizenSender {
    void sendBusInfo(Object busInfo);
    void sendTravelTimeInfo(Object travelTime);
    void sendStopInfo(Object stopInfo);
}
