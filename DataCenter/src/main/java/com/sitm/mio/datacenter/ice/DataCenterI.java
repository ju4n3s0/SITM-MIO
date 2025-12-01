package com.sitm.mio.datacenter.ice;

import com.zeroc.Ice.Current;
import SITM.CitizenInfoRequest;
import SITM.CitizenInfoResponse;
import com.sitm.mio.datacenter.interfaces.IDataCenterFacade;
import com.sitm.mio.datacenter.component.TravelTimeCalculator;

/**
 * ICE Servant implementation for DataCenter interface.
 * Wraps the internal DataCenterFacade to expose it via ICE.
 */
public class DataCenterI implements SITM.DataCenter {
    
    private final IDataCenterFacade facade;
    private final TravelTimeCalculator travelTimeCalculator;
    
    public DataCenterI(IDataCenterFacade facade, TravelTimeCalculator travelTimeCalculator) {
        this.facade = facade;
        this.travelTimeCalculator = travelTimeCalculator;
    }
    
    @Override
    public CitizenInfoResponse getCitizenInformation(CitizenInfoRequest request, Current current) {
        System.out.println("[DataCenterI] ICE request: getCitizenInformation(" + 
                          request.originStopId + " -> " + request.destinationStopId + ")");
        
        // Calculate actual travel time using TravelTimeCalculator
        String message = travelTimeCalculator.calculateTravelTime(
            request.originStopId, 
            request.destinationStopId
        );
        
        CitizenInfoResponse response = new CitizenInfoResponse();
        response.message = message;
        response.fromCache = false;
        response.timestamp = System.currentTimeMillis();
        
        return response;
    }
    
    @Override
    public void submitTravelTime(SITM.TravelTimeSubmission submission, Current current) {
        System.out.println(String.format(
            "[DataCenterI] ICE request: submitTravelTime(%d -> %d: %.1f min, %d samples)",
            submission.originStopId, submission.destinationStopId, 
            submission.avgTimeMinutes, submission.sampleCount
        ));
        
        try {
            // Create TravelTimeStat from submission
            com.sitm.mio.datacenter.model.TravelTimeStat stat = 
                new com.sitm.mio.datacenter.model.TravelTimeStat(
                    submission.zoneId,
                    submission.originStopId,
                    submission.destinationStopId,
                    submission.avgTimeMinutes,
                    submission.sampleCount,
                    java.time.Instant.now()
                );
            
            // Save via facade
            facade.saveTravelTimeStat(stat);
            
            System.out.println("[DataCenterI] Travel time submitted successfully for zone: " + submission.zoneId);
            
        } catch (Exception e) {
            System.err.println("[DataCenterI] Error saving travel time: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean ping(Current current) {
        System.out.println("[DataCenterI] ICE ping received");
        return true;
    }
}
