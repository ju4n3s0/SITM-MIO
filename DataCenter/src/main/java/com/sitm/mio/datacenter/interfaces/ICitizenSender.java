package com.sitm.mio.datacenter.interfaces;

/**
 * Interface for sending data to the Citizen module/system.
 * Handles communication of bus information, travel times, and stop data.
 * 
 * Realized by: CitizenSender
 */
public interface ICitizenSender {
    void sendBusInfo(Object busInfo);
    void sendTravelTimeInfo(Object travelTime);
    void sendStopInfo(Object stopInfo);
}
