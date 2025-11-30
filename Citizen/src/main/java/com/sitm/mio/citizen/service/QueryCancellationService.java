package com.sitm.mio.citizen.service;

/**
 * Service responsible for canceling ongoing queries.
 * Allows citizens to stop a travel time query that is in progress.
 */
public class QueryCancellationService {

    /**
     * Cancel a query by its request key.
     * @param requestKey The unique key identifying the request (e.g., "origin-destination")
     */
    public void cancelQuery(String requestKey) {
        System.out.println("Canceling query with key: " + requestKey);
    }
}
