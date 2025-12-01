// Slice definitions for DataCenter module
// Defines the interface that ProxyServer uses to communicate with DataCenter

module SITM {
    
    /**
     * Enriched datagram event with zone/arc information.
     * Published from DataCenter to ProxyServer.
     */
    struct EnrichedDatagram {
        long datagramId;
        long busId;
        long lineId;
        double latitude;
        double longitude;
        string zoneId;
        string arcId;
        long timestamp;  // Unix timestamp in milliseconds
    };
    
    /**
     * Citizen information request/response.
     * Used for travel time queries.
     */
    struct CitizenInfoRequest {
        long originStopId;
        long destinationStopId;
    };
    
    struct CitizenInfoResponse {
        string message;  // e.g., "Travel time: 15 minutes"
        bool fromCache;
        long timestamp;
    };
    
    /**
     * DataCenter interface exposed to ProxyServer.
     * This is the main service interface.
     */
    interface DataCenter {
        /**
         * Get citizen travel information between two stops.
         * ProxyServer calls this when cache misses.
         * 
         * @param request Origin and destination stop IDs
         * @return Travel time information
         */
        CitizenInfoResponse getCitizenInformation(CitizenInfoRequest request);
        
        /**
         * Health check - verify DataCenter is responsive.
         * @return true if healthy
         */
        bool ping();
    };
    
    /**
     * Event publisher interface for DataCenter.
     * DataCenter publishes enriched datagrams to subscribers (ProxyServer).
     */
    interface DataCenterEventPublisher {
        /**
         * Subscribe to enriched datagram events.
         * ProxyServer calls this to register for events.
         * 
         * @param subscriber The subscriber proxy
         */
        void subscribe(DataCenterEventSubscriber* subscriber);
        
        /**
         * Unsubscribe from events.
         */
        void unsubscribe(DataCenterEventSubscriber* subscriber);
    };
    
    /**
     * Event subscriber interface.
     * ProxyServer implements this to receive enriched datagrams.
     */
    interface DataCenterEventSubscriber {
        /**
         * Receive an enriched datagram event.
         * Called by DataCenter when new data arrives.
         * 
         * @param datagram The enriched datagram
         */
        void onEnrichedDatagram(EnrichedDatagram datagram);
    };
};
