package com.sitm.mio.proxyserver.interfaces;

import com.sitm.mio.proxyserver.dto.GetCitizenInformationRequest;
import com.sitm.mio.proxyserver.dto.CitizenInformation;

/**
 * Interface for cache service operations.
 * Defines the contract for obtaining citizen information through the proxy cache.
 */
public interface ICacheService {

    /**
     * Get citizen information for a travel time query.
     * @param request The request containing origin and destination stop IDs
     * @return CitizenInformation with travel time details, or null if not available
     */
    CitizenInformation getCitizenInformation(GetCitizenInformationRequest request);
}
