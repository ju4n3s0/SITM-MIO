package com.sitm.mio.proxyserver.datacenter;

import SITM.CitizenInformation;
import com.sitm.mio.proxyserver.interfaces.IDataCenterService;
import com.sitm.mio.proxyserver.config.ConfigLoader;

/**
 * Client for communicating with the remote DataCenter service.
 * 
 * This is a stub implementation. In production, this would:
 * - Make HTTP/REST calls to the DataCenter server
 * - Handle network errors and timeouts
 * - Implement retry logic
 * - Use proper serialization/deserialization
 */
public class DataCenterClient implements IDataCenterService {
    
    private final String dataCenterUrl;
    private final int timeout;
    
    public DataCenterClient() {
        // Load from configuration
        this.dataCenterUrl = ConfigLoader.getDataCenterUrl();
        this.timeout = ConfigLoader.getDataCenterTimeout();
        
        System.out.println("DataCenterClient initialized:");
        System.out.println("  URL: " + dataCenterUrl);
        System.out.println("  Timeout: " + timeout + "s");
    }
    
    public DataCenterClient(String dataCenterUrl) {
        this.dataCenterUrl = dataCenterUrl;
        this.timeout = ConfigLoader.getDataCenterTimeout();
    }
    
    @Override
    public CitizenInformation getCitizenInformation(long originId, long destinationId) {
        // TODO: Implement actual HTTP call to DataCenter
        // For now, return a stub response
        
        System.out.println("DataCenterClient: Querying DataCenter at " + dataCenterUrl);
        System.out.println("  Origin: " + originId);
        System.out.println("  Destination: " + destinationId);
        
        // Stub response - replace with actual HTTP call
        String message = "Travel time from " + originId + 
                        " to " + destinationId + ": 25 minutes (estimated)";
        
        var info = new CitizenInformation();
        info.message = message;
        return info;
    }
}
