// Slice definitions for DataCenter module
// Defines interfaces for route calculations and travel time queries

module SITM {
    
    // Data structure for citizen information (reused from ProxyServer.ice)
    // Note: In ICE, we can import from other slice files
    // For simplicity, we'll redefine it here
    struct CitizenInformation {
        string message;
    };
    
    // DataCenter interface for route calculations
    interface DataCenter {
        /**
         * Calculate travel time between two stops.
         * @param originId Origin stop identifier
         * @param destinationId Destination stop identifier
         * @return CitizenInformation with calculated travel time
         */
        CitizenInformation calculateTravelTime(long originId, long destinationId);
        
        /**
         * Get route information between stops.
         * @param originId Origin stop identifier
         * @param destinationId Destination stop identifier
         * @return Route details as string
         */
        string getRouteInformation(long originId, long destinationId);
    };
};
