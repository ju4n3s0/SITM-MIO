package com.sitm.mio.citizen.proxyserver;

import com.sitm.mio.citizen.dto.GetCitizenInformationRequest;
import com.sitm.mio.citizen.dto.CitizenInformation;

/**
 * Interface for ProxyServer cache service operations.
 * 
 * Note: This is a local interface for Citizen module.
 * The actual ProxyServer runs on a different machine and communicates via network.
 */
public interface ICacheService {
    
    /**
     * Get citizen information from ProxyServer.
     * @param request The request containing origin and destination stop IDs
     * @return CitizenInformation with travel time details, or null if not available
     */
    CitizenInformation getCitizenInformation(GetCitizenInformationRequest request);
}
