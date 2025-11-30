package com.sitm.mio.citizen.cache;

import java.util.HashMap;
import java.util.Map;

import com.sitm.mio.citizen.proxyserver.ICacheService;
import com.sitm.mio.citizen.dto.CitizenInformation;
import com.sitm.mio.citizen.dto.GetCitizenInformationRequest;
import com.sitm.mio.citizen.service.QueryCancellationService;

/** 
 * Client-side cache for travel time information.
 * Caches responses from the proxy server to reduce network calls.
 */
public class ClientCache {

    /** Local cache storage */
    private final Map<String, CitizenInformation> cache = new HashMap<>();

    /** Remote cache service to delegate queries to */
    private final ICacheService proxyService;

    /** Cancellation service used to stop queries */
    private final QueryCancellationService cancellationService;

    public ClientCache(ICacheService proxyService, QueryCancellationService cancellationService) {
        this.proxyService = proxyService;
        this.cancellationService = cancellationService;
    }

    /**
     * Query travel time information.
     * Checks local cache first, then delegates to proxy server if not found.
     * @param request The request containing origin and destination
     * @return CitizenInformation or null if not available
     */
    public CitizenInformation query(GetCitizenInformationRequest request) {
        String key = request.getOriginId() + "-" + request.getDestinationId();
        CitizenInformation info = cache.get(key);
        if (info == null) {
            info = proxyService.getCitizenInformation(request);
            if (info != null) {
                cache.put(key, info);
            }
        }
        return info;
    }

    /**
     * Cancel an ongoing query.
     * @param requestKey The key identifying the request to cancel
     */
    public void cancel(String requestKey) {
        cancellationService.cancelQuery(requestKey);
    }
}
