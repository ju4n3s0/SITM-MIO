package com.sitm.mio.datacenter.component;

import com.sitm.mio.datacenter.interfaces.ICitizenSender;

/**
 * Sends data to citizen system.
 * Component from deployment diagram: SenderCiudadanos
 * 
 * Realizes: ICitizenSender
 */
public class CitizenSender implements ICitizenSender {
    
    @Override
    public void sendBusInfo(Object busInfo) {
        // TODO: Send bus information to citizen system
        System.out.println("Sending bus info to citizens");
    }
    
    @Override
    public void sendTravelTimeInfo(Object travelTime) {
        // TODO: Send travel time information
        System.out.println("Sending travel time to citizens");
    }
    
    @Override
    public void sendStopInfo(Object stopInfo) {
        // TODO: Send stop information
        System.out.println("Sending stop info to citizens");
    }
}
