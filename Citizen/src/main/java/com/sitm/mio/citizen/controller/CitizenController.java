package com.sitm.mio.citizen.controller;

import SITM.CitizenInformation;
import com.sitm.mio.citizen.ui.CitizenUI;
import com.sitm.mio.citizen.proxyserver.ProxyServerClientICE;

/**
 * Controller for the Citizen application.
 * Coordinates between the UI, services, and cache layers.
 */
public class CitizenController {

    private final CitizenUI view;
    private final ProxyServerClientICE proxyClient;

    public CitizenController(CitizenUI view) {
        this.view = view;
        this.proxyClient = new ProxyServerClientICE();
    }

    /**
     * Query travel time information for given origin and destination stops.
     * @param originId The origin stop ID
     * @param destinationId The destination stop ID
     */
    public void query(long originId, long destinationId) {
        CitizenInformation info = proxyClient.getCitizenInformation(originId, destinationId);
        if (info == null || info.message == null || info.message.isEmpty()) {
            view.showCitizenInformation("Could not obtain information for stops " + originId + " → " + destinationId);
        } else {
            view.showCitizenInformation(info.message);
        }
    }

    /**
     * Shutdown the ICE client when done.
     */
    public void shutdown() {
        if (proxyClient != null) {
            proxyClient.shutdown();
        }
    }
}