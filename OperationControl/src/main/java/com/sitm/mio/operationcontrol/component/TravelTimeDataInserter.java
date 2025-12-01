package com.sitm.mio.operationcontrol.component;

import SITM.ProxyServerPrx;
import SITM.TravelTimeSubmission;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

/**
 * Submits travel time statistics to DataCenter via ProxyServer ICE.
 * 
 * OperationControl calculates travel times and sends them through ProxyServer
 * so that Citizens can query historical travel time data.
 * 
 * Architecture: OperationControl → ProxyServer → DataCenter
 */
public class TravelTimeDataInserter {
    
    private final ProxyServerPrx proxyServer;
    private final Communicator communicator;
    
    public TravelTimeDataInserter(String proxyHost, int proxyPort) {
        String endpoint = String.format("tcp -h %s -p %d", proxyHost, proxyPort);
        
        System.out.println("[TravelTimeDataInserter] Connecting to ProxyServer at " + endpoint);
        
        try {
            this.communicator = Util.initialize();
            ObjectPrx base = communicator.stringToProxy("ProxyServer:" + endpoint);
            this.proxyServer = ProxyServerPrx.checkedCast(base);
            
            if (this.proxyServer == null) {
                throw new RuntimeException("Invalid ProxyServer proxy");
            }
            
            System.out.println("[TravelTimeDataInserter] Connected to ProxyServer via ICE");
            
        } catch (Exception e) {
            System.err.println("[TravelTimeDataInserter] Failed to connect: " + e.getMessage());
            throw new RuntimeException("ICE initialization failed", e);
        }
    }
    
    /**
     * Submit travel time statistics to DataCenter via ICE.
     * 
     * @param zoneId Zone identifier
     * @param originStopId Origin stop ID
     * @param destinationStopId Destination stop ID
     * @param avgTimeMinutes Average travel time in minutes
     * @param sampleCount Number of samples used for calculation
     */
    public void insertTravelTime(String zoneId, long originStopId, long destinationStopId, 
                                 double avgTimeMinutes, int sampleCount) {
        try {
            // Create ICE submission struct
            TravelTimeSubmission submission = new TravelTimeSubmission();
            submission.zoneId = zoneId;
            submission.originStopId = originStopId;
            submission.destinationStopId = destinationStopId;
            submission.avgTimeMinutes = avgTimeMinutes;
            submission.sampleCount = sampleCount;
            
            // Submit to DataCenter via ProxyServer
            proxyServer.submitTravelTime(submission);
            
            System.out.println(String.format(
                "[TravelTimeDataInserter] Submitted via ProxyServer: Zone %s, %d -> %d: %.1f min (%d samples)",
                zoneId, originStopId, destinationStopId, avgTimeMinutes, sampleCount
            ));
            
        } catch (Exception e) {
            System.err.println("[TravelTimeDataInserter] Error submitting travel time: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Insert travel time from calculation result.
     * 
     * @param zoneId Zone identifier
     * @param originStopId Origin stop ID
     * @param destinationStopId Destination stop ID
     * @param result Calculation result map from CalculateAverageTime
     */
    public void insertFromCalculationResult(String zoneId, long originStopId, long destinationStopId, 
                                           java.util.Map<String, Object> result) {
        if (result == null || !"SUCCESS".equals(result.get("status"))) {
            System.out.println("[TravelTimeDataInserter] Skipping insert - calculation not successful");
            return;
        }
        
        try {
            double avgTime = ((Number) result.get("averageTimeMinutes")).doubleValue();
            int sampleCount = ((Number) result.get("sampleCount")).intValue();
            
            insertTravelTime(zoneId, originStopId, destinationStopId, avgTime, sampleCount);
            
        } catch (Exception e) {
            System.err.println("[TravelTimeDataInserter] Error processing result: " + e.getMessage());
        }
    }
    
    /**
     * Shutdown the ICE communicator.
     */
    public void shutdown() {
        if (communicator != null) {
            try {
                communicator.destroy();
                System.out.println("[TravelTimeDataInserter] ICE communicator shut down");
            } catch (Exception e) {
                System.err.println("[TravelTimeDataInserter] Error shutting down: " + e.getMessage());
            }
        }
    }
}
