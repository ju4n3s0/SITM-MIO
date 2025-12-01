package com.sitm.mio.citizen.service;

import com.sitm.mio.citizen.dto.CitizenInformation;
import com.sitm.mio.citizen.dto.GetCitizenInformationRequest;
import com.sitm.mio.citizen.cache.ClientCache;

/**
 * Service for querying travel time information.
 * Acts as a facade to the cache layer.
 */
public class TravelTimeService {
    private final ClientCache cache;

    public TravelTimeService(ClientCache cache) {
        this.cache = cache;
    }

    /**
     * Query travel time information for a given request.
     * @param request The request containing origin and destination stop IDs
     * @return CitizenInformation with travel time details
     */
    public CitizenInformation query(GetCitizenInformationRequest request) {
        return cache.query(request);
    }
}
