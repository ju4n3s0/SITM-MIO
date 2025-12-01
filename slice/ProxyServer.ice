// Slice definitions for ProxyServer module
// Defines interfaces for Citizen to query travel time information

module SITM {
    
    // Data structure for citizen information response
    struct CitizenInformation {
        string message;
    };
    
    // Main interface for Citizen queries
    interface ProxyServer {
        /**
         * Query travel time information between two stops.
         * @param originId Origin stop identifier
         * @param destinationId Destination stop identifier
         * @return CitizenInformation with travel time details
         */
        CitizenInformation getCitizenInformation(long originId, long destinationId);
    };
    
    // Health check interface
    interface HealthCheck {
        /**
         * Check if service is alive.
         * @return true if service is running
         */
        bool ping();
        
        /**
         * Get service status information.
         * @return Status message
         */
        string getStatus();
    };
};
