package com.sitm.mio.datacenter.component;

import com.sitm.mio.datacenter.interfaces.IController;
import com.sitm.mio.datacenter.interfaces.IEventBus;
import com.sitm.mio.datacenter.interfaces.IArcZoneResolver;
import com.sitm.mio.datacenter.interfaces.IDataCenterFacade;
import com.sitm.mio.datacenter.ice.DataCenterEventPublisherI;
import com.sitm.mio.datacenter.model.EventNewDatagram;
import SITM.EnrichedDatagram;

/**
 * Main controller orchestrating DataCenter operations.
 * Component from deployment diagram: Controlador
 * 
 * Realizes: IController
 * Uses: IEventBus, IArcZoneResolver, IDataCenterFacade
 */
public class Controller implements IController {
    
    private final IEventBus eventBus;
    private final IArcZoneResolver arcZoneResolver;
    private final IDataCenterFacade facade;
    private DataCenterEventPublisherI icePublisher;
    
    public Controller(IEventBus eventBus, IArcZoneResolver arcZoneResolver, IDataCenterFacade facade) {
        this.eventBus = eventBus;
        this.arcZoneResolver = arcZoneResolver;
        this.facade = facade;
    }
    
    /**
     * Set the ICE publisher for sending enriched datagrams to ProxyServer.
     * Called from Main after ICE initialization.
     */
    public void setIcePublisher(DataCenterEventPublisherI icePublisher) {
        this.icePublisher = icePublisher;
    }
    
    @Override
    public void start() {
        // TODO: Start controller
        System.out.println("Controller started");
    }
    
    @Override
    public void stop() {
        // TODO: Stop controller
        System.out.println("Controller stopped");
    }
    
    @Override
    public void processDatagram(Object datagram) {
        if (!(datagram instanceof EventNewDatagram)) {
            System.err.println("[Controller] Received non-EventNewDatagram: " + datagram.getClass());
            return;
        }
        
        EventNewDatagram event = (EventNewDatagram) datagram;
        
        System.out.println("[Controller] Processing datagram " + event.getDatagramId() + 
                          " from bus " + event.getBusId());
        
        // 1. Resolve zone from GPS coordinates
        String zoneId = arcZoneResolver.getZoneId(event.getLatitude(), event.getLongitude());
        
        // 2. Create enriched datagram for ICE
        EnrichedDatagram enriched = new EnrichedDatagram();
        enriched.datagramId = event.getDatagramId();
        enriched.busId = event.getBusId();
        enriched.lineId = event.getLineId();
        enriched.latitude = event.getLatitude();
        enriched.longitude = event.getLongitude();
        enriched.zoneId = zoneId;
        enriched.arcId = "ARC_" + zoneId; // TODO: Implement proper arc resolution
        enriched.timestamp = event.getEventTimestamp().toEpochMilli();
        
        System.out.println("[Controller] Enriched datagram with zone: " + zoneId);
        
        // 3. Publish to ProxyServer via ICE
        if (icePublisher != null) {
            icePublisher.publishEnrichedDatagram(enriched);
        } else {
            System.err.println("[Controller] ICE publisher not set - cannot publish enriched datagram");
        }
    }
    
    @Override
    public Object handleQuery(String queryType, Object params) {
        // TODO: Handle queries
        System.out.println("Handling query: " + queryType);
        return null;
    }
}
