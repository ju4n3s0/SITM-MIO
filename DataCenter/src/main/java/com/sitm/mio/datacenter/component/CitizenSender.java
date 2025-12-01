package com.sitm.mio.datacenter.component;

import com.sitm.mio.datacenter.interfaces.ICitizenSender;

/**
 * Sends data to the Citizen module/system.
 * Handles communication of bus information, travel times, and stop data to citizens.
 * 
 * Component from deployment diagram: CitizenSender (formerly SenderCiudadanos)
 * 
 * Realizes: ICitizenSender
 */
public class CitizenSender implements ICitizenSender {
    
    @Override
    public void sendBusInfo(Object busInfo) {
        // TODO: Send bus information to citizen system
        // This could be via:
        // - REST API call
        // - Message queue (RabbitMQ, Kafka)
        // - WebSocket push
        // - ICE RPC call
        System.out.println("[CitizenSender] Sending bus info to citizens: " + busInfo);
    }
    
    @Override
    public void sendTravelTimeInfo(Object travelTime) {
        // TODO: Send travel time information to citizen system
        System.out.println("[CitizenSender] Sending travel time to citizens: " + travelTime);
    }
    
    @Override
    public void sendStopInfo(Object stopInfo) {
        // TODO: Send stop information to citizen system
        System.out.println("[CitizenSender] Sending stop info to citizens: " + stopInfo);
    }
}
