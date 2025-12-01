package com.sitm.mio.citizen.controller;

import com.sitm.mio.citizen.cache.ClientCache;
import com.sitm.mio.citizen.dto.CitizenInformation;
import com.sitm.mio.citizen.dto.GetCitizenInformationRequest;
import com.sitm.mio.citizen.service.TravelTimeService;
import com.sitm.mio.citizen.service.QueryCancellationService;
import com.sitm.mio.citizen.ui.CitizenUI;
import com.sitm.mio.citizen.proxyserver.ICacheService;
import com.sitm.mio.citizen.proxyserver.ProxyServerClient;

/**
 * Controller for the Citizen application.
 * Coordinates between the UI, services, and cache layers.
 */
public class CitizenController {

    private final CitizenUI view;
    private final ClientCache cache;
    private final TravelTimeService service;
    private final QueryCancellationService cancellationService;

    public CitizenController(CitizenUI view) {
        this.view = view;
        
        this.cancellationService = new QueryCancellationService();
        ICacheService proxy = new ProxyServerClient();
        this.cache = new ClientCache(proxy, cancellationService);
        this.service = new TravelTimeService(cache);
    }

    /**
     * Query travel time information for given origin and destination stops.
     * @param originId The origin stop ID
     * @param destinationId The destination stop ID
     */
    public void query(long originId, long destinationId) {
        GetCitizenInformationRequest request = new GetCitizenInformationRequest(originId, destinationId);
        CitizenInformation info = service.query(request);
        if (info == null) {
            view.showCitizenInformation("Could not obtain information for stops " + originId + " → " + destinationId);
        } else {
            view.showCitizenInformation(info.getMessage());
        }
    }

    /**
     * Cancel an ongoing query.
     * @param requestKey The key identifying the request to cancel
     */
    public void cancelQuery(String requestKey) {
        cache.cancel(requestKey);
        view.showCitizenInformation("Cancellation requested for query " + requestKey);
    }
}