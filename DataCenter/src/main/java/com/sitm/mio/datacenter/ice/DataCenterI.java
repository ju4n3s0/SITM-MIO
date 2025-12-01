package com.sitm.mio.datacenter.ice;

import com.zeroc.Ice.Current;
import SITM.CitizenInfoRequest;
import SITM.CitizenInfoResponse;
import com.sitm.mio.datacenter.interfaces.IDataCenterFacade;

/**
 * ICE Servant implementation for DataCenter interface.
 * Wraps the internal DataCenterFacade to expose it via ICE.
 */
public class DataCenterI implements SITM.DataCenter {
    
    private final IDataCenterFacade facade;
    
    public DataCenterI(IDataCenterFacade facade) {
        this.facade = facade;
    }
    
    @Override
    public CitizenInfoResponse getCitizenInformation(CitizenInfoRequest request, Current current) {
        System.out.println("[DataCenterI] ICE request: getCitizenInformation(" + 
                          request.originStopId + " -> " + request.destinationStopId + ")");
        
        // TODO: Implement actual travel time calculation
        // For now, return a mock response
        CitizenInfoResponse response = new CitizenInfoResponse();
        response.message = "Travel time: 15 minutes (mock)";
        response.fromCache = false;
        response.timestamp = System.currentTimeMillis();
        
        return response;
    }
    
    @Override
    public boolean ping(Current current) {
        System.out.println("[DataCenterI] ICE ping received");
        return true;
    }
}
