package com.sitm.mio.proxyserver.interfaces;

import SITM.CitizenInformation;

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
    CitizenInformation getCitizenInformation(long originId, long destinationId);
}
