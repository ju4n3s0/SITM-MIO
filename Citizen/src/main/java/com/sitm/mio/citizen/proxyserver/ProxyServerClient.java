package com.sitm.mio.citizen.proxyserver;

import com.sitm.mio.citizen.dto.GetCitizenInformationRequest;
import com.sitm.mio.citizen.dto.CitizenInformation;
import com.sitm.mio.citizen.config.ConfigLoader;

/**
 * Client for communicating with the remote ProxyServer service.
 * 
 * This is a stub implementation. In production, this would:
 * - Make HTTP/REST calls to the ProxyServer
 * - Handle network errors and timeouts
 * - Implement retry logic
 * - Use proper serialization/deserialization (JSON)
 * - Handle authentication/authorization
 */
public class ProxyServerClient implements ICacheService {
    
    private final String proxyServerUrl;
    private final int timeout;
    private final int retryAttempts;
    
    public ProxyServerClient() {
        // Load from configuration
        this.proxyServerUrl = ConfigLoader.getProxyServerUrl();
        this.timeout = ConfigLoader.getProxyServerTimeout();
        this.retryAttempts = ConfigLoader.getRetryAttempts();
        
        System.out.println("ProxyServerClient initialized:");
        System.out.println("  URL: " + proxyServerUrl);
        System.out.println("  Timeout: " + timeout + "s");
        System.out.println("  Retry attempts: " + retryAttempts);
    }
    
    public ProxyServerClient(String proxyServerUrl) {
        this.proxyServerUrl = proxyServerUrl;
        this.timeout = ConfigLoader.getProxyServerTimeout();
        this.retryAttempts = ConfigLoader.getRetryAttempts();
    }
    
    @Override
    public CitizenInformation getCitizenInformation(GetCitizenInformationRequest request) {
        // TODO: Implement actual HTTP call to ProxyServer
        // Example using HttpClient:
        // - POST to proxyServerUrl + "/query"
        // - Send JSON: {"originId": request.getOriginId(), "destinationId": request.getDestinationId()}
        // - Parse JSON response to CitizenInformation
        
        System.out.println("ProxyServerClient: Querying ProxyServer at " + proxyServerUrl);
        System.out.println("  Origin: " + request.getOriginId());
        System.out.println("  Destination: " + request.getDestinationId());
        
        // Stub response - replace with actual HTTP call
        try {
            // Simulate network delay
            Thread.sleep(100);
            
            String message = "Travel time from stop " + request.getOriginId() + 
                            " to stop " + request.getDestinationId() + 
                            ": 15-20 minutes (via ProxyServer)";
            
            return new CitizenInformation(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new CitizenInformation("Error: Request interrupted");
        }
    }
}
