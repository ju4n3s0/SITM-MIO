// Slice definitions for ProxyServer module
// Defines interfaces for Citizen to query travel time information

module SITM {
    
    // Data structure for citizen information response
    struct CitizenInformation {
        string message;
    };
    
    // Data structure for travel time submission from OperationControl
    struct TravelTimeSubmission {
        string zoneId;
        long originStopId;
        long destinationStopId;
        double avgTimeMinutes;
        int sampleCount;
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
        
        /**
         * Submit travel time statistics from OperationControl.
         * ProxyServer forwards this to DataCenter for storage.
         * @param submission Travel time data
         */
        void submitTravelTime(TravelTimeSubmission submission);
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
