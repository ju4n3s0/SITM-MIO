package com.sitm.mio.proxyserver.datacenter;

import SITM.CitizenInformation;
import SITM.CitizenInfoResponse;
import com.sitm.mio.proxyserver.interfaces.IDataCenterService;
import com.sitm.mio.proxyserver.config.ConfigLoader;

/**
 * HTTP-based DataCenter client (legacy/fallback).
 * 
 * NOTE: This is a stub implementation for backward compatibility.
 * The actual ICE-based client is in com.sitm.mio.proxyserver.ice.DataCenterClient
 * 
 * This would be used if:
 * - DataCenter doesn't support ICE yet
 * - Fallback to HTTP/REST is needed
 * - Testing without ICE infrastructure
 */
public class DataCenterClient implements IDataCenterService {
    
    private final String dataCenterUrl;
    private final int timeout;
    private com.sitm.mio.proxyserver.ice.DataCenterClient iceClient;
    
    public DataCenterClient() {
        // Load from configuration
        this.dataCenterUrl = ConfigLoader.getDataCenterUrl();
        this.timeout = ConfigLoader.getDataCenterTimeout();
        
        System.out.println("[HTTP DataCenterClient] Initialized (legacy mode):");
        System.out.println("  URL: " + dataCenterUrl);
        System.out.println("  Timeout: " + timeout + "s");
        System.out.println("  NOTE: Use ICE client for production");
    }
    
    public DataCenterClient(String dataCenterUrl) {
        this.dataCenterUrl = dataCenterUrl;
        this.timeout = ConfigLoader.getDataCenterTimeout();
    }
    
    /**
     * Set the ICE client for delegation.
     * If set, this HTTP client will delegate to ICE client.
     */
    public void setIceClient(com.sitm.mio.proxyserver.ice.DataCenterClient iceClient) {
        this.iceClient = iceClient;
    }
    
    @Override
    public CitizenInformation getCitizenInformation(long originId, long destinationId) {
        // If ICE client is available, use it
        if (iceClient != null) {
            try {
                CitizenInfoResponse iceResponse = iceClient.getCitizenInformation(originId, destinationId);
                
                // Convert ICE response to legacy format
                CitizenInformation info = new CitizenInformation();
                info.message = iceResponse.message;
                return info;
            } catch (Exception e) {
                System.err.println("[HTTP DataCenterClient] ICE call failed, using stub: " + e.getMessage());
            }
        }
        
        // Fallback: stub response
        System.out.println("[HTTP DataCenterClient] Using stub response (no ICE)");
        System.out.println("  Origin: " + originId);
        System.out.println("  Destination: " + destinationId);
        
        String message = "Travel time from " + originId + 
                        " to " + destinationId + ": 25 minutes (estimated - stub)";
        
        CitizenInformation info = new CitizenInformation();
        info.message = message;
        return info;
    }
}
