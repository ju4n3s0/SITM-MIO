package com.sitm.mio.proxyserver.service;

import SITM.CitizenInformation;
import SITM.CitizenInfoResponse;
import SITM.CitizenInfoRequest;
import SITM.TravelTimeSubmission;
import com.sitm.mio.proxyserver.ice.DataCenterClient;
import com.sitm.mio.proxyserver.cache.CacheManager;
import com.sitm.mio.proxyserver.cache.CacheType;

/**
 * Request router that handles requests from citizens.
 * Routes requests through cache and delegates to DataCenter via ICE.
 */
public class RequestRouter {

    private final CacheManager cacheManager;
    private DataCenterClient dataCenterClient;

    public RequestRouter() {
        this.cacheManager = new CacheManager();
    }
    
    /**
     * Set the DataCenter ICE client.
     * Must be called before routing requests.
     */
    public void setDataCenterClient(DataCenterClient client) {
        this.dataCenterClient = client;
    }

    public CitizenInformation getCitizenInformation(long originId, long destinationId) {
        String key = originId + "-" + destinationId;
        
        // Check cache first
        CitizenInformation info = cacheManager.get(key);
        if (info != null) {
            System.out.println("Cache HIT for key: " + key);
            return info;
        }
        
        System.out.println("Cache MISS for key: " + key);
        
        // Query DataCenter via ICE
        if (dataCenterClient == null) {
            System.err.println("[RequestRouter] DataCenter client not initialized!");
            CitizenInformation errorInfo = new CitizenInformation();
            errorInfo.message = "Error: DataCenter not available";
            return errorInfo;
        }
        
        try {
            CitizenInfoResponse iceResponse = dataCenterClient.getCitizenInformation(originId, destinationId);
            
            // Convert ICE response to CitizenInformation
            CitizenInformation dcInfo = new CitizenInformation();
            dcInfo.message = iceResponse.message;
            
            // Store in cache for future requests
            cacheManager.put(key, dcInfo, CacheType.CITIZEN);
            
            return dcInfo;
            
        } catch (Exception e) {
            System.err.println("[RequestRouter] Error querying DataCenter: " + e.getMessage());
            CitizenInformation errorInfo = new CitizenInformation();
            errorInfo.message = "Error: " + e.getMessage();
            return errorInfo;
        }
    }
    
    /**
     * Get the cache manager instance.
     * Used by AnalyticsService to collect cache statistics.
     */
    public CacheManager getCacheManager() {
        return cacheManager;
    }
    
    /**
     * Submit travel time statistics from OperationControl to DataCenter.
     * ProxyServer acts as intermediary.
     */
    public void submitTravelTime(TravelTimeSubmission submission) {
        System.out.println("[RequestRouter] Forwarding travel time submission to DataCenter");
        System.out.println("  Zone: " + submission.zoneId);
        System.out.println("  Route: " + submission.originStopId + " -> " + submission.destinationStopId);
        
        if (dataCenterClient == null) {
            System.err.println("[RequestRouter] DataCenter client not initialized!");
            return;
        }
        
        try {
            dataCenterClient.submitTravelTime(submission);
            System.out.println("[RequestRouter] Travel time submitted successfully");
        } catch (Exception e) {
            System.err.println("[RequestRouter] Error submitting travel time: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get the DataCenter client instance.
     */
    public DataCenterClient getDataCenterClient() {
        return dataCenterClient;
    }
}