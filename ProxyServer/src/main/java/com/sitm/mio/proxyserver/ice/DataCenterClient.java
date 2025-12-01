package com.sitm.mio.proxyserver.ice;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;
import SITM.DataCenterPrx;
import SITM.DataCenterEventPublisherPrx;
import SITM.DataCenterEventSubscriberPrx;
import SITM.CitizenInfoRequest;
import SITM.CitizenInfoResponse;
import com.sitm.mio.proxyserver.cache.CacheManager;
import com.sitm.mio.proxyserver.config.ConfigLoader;

/**
 * ICE Client for communicating with DataCenter.
 * 
 * Responsibilities:
 * - Connect to DataCenter ICE server
 * - Query citizen information (travel times)
 * - Subscribe to enriched datagram events
 * - Manage ICE communicator lifecycle
 */
public class DataCenterClient {
    
    private Communicator communicator;
    private DataCenterPrx dataCenterProxy;
    private DataCenterEventPublisherPrx eventPublisherProxy;
    private DataCenterEventSubscriberI subscriberServant;
    private ObjectAdapter adapter;
    private final CacheManager cacheManager;
    
    public DataCenterClient(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    /**
     * Initialize ICE connection to DataCenter.
     * 
     * @param args Command-line arguments for ICE
     */
    public void initialize(String[] args) {
        try {
            System.out.println("[DataCenterClient] Initializing ICE connection...");
            
            // Create ICE communicator
            communicator = Util.initialize(args);
            
            // Get DataCenter proxy from configuration
            String dataCenterProxyStr = ConfigLoader.getDataCenterProxy();
            System.out.println("[DataCenterClient] DataCenter endpoint: " + dataCenterProxyStr);
            
            ObjectPrx base = communicator.stringToProxy(dataCenterProxyStr);
            dataCenterProxy = DataCenterPrx.checkedCast(base);
            
            if (dataCenterProxy == null) {
                throw new RuntimeException("Invalid DataCenter proxy: " + dataCenterProxyStr);
            }
            
            // Get EventPublisher proxy
            String eventPublisherProxyStr = ConfigLoader.getDataCenterEventPublisherProxy();
            System.out.println("[DataCenterClient] EventPublisher endpoint: " + eventPublisherProxyStr);
            
            ObjectPrx publisherBase = communicator.stringToProxy(eventPublisherProxyStr);
            eventPublisherProxy = DataCenterEventPublisherPrx.checkedCast(publisherBase);
            
            if (eventPublisherProxy == null) {
                throw new RuntimeException("Invalid EventPublisher proxy: " + eventPublisherProxyStr);
            }
            
            // Create object adapter for receiving events
            String adapterEndpoints = ConfigLoader.getProxyServerAdapterEndpoints();
            adapter = communicator.createObjectAdapterWithEndpoints("ProxyServerEventAdapter", adapterEndpoints);
            
            // Create subscriber servant
            subscriberServant = new DataCenterEventSubscriberI(cacheManager);
            
            System.out.println("[DataCenterClient] ICE connection initialized successfully");
            
        } catch (Exception e) {
            System.err.println("[DataCenterClient] Failed to initialize: " + e.getMessage());
            throw new RuntimeException("DataCenter ICE initialization failed", e);
        }
    }
    
    /**
     * Test connection to DataCenter.
     * 
     * @return true if DataCenter responds to ping
     */
    public boolean ping() {
        try {
            boolean result = dataCenterProxy.ping();
            System.out.println("[DataCenterClient] Ping successful: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("[DataCenterClient] Ping failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Query citizen information from DataCenter.
     * 
     * @param originStopId Origin stop ID
     * @param destinationStopId Destination stop ID
     * @return Citizen information response
     */
    public CitizenInfoResponse getCitizenInformation(long originStopId, long destinationStopId) {
        try {
            CitizenInfoRequest request = new CitizenInfoRequest();
            request.originStopId = originStopId;
            request.destinationStopId = destinationStopId;
            
            System.out.println("[DataCenterClient] Querying citizen info:");
            System.out.println("  Origin: " + originStopId);
            System.out.println("  Destination: " + destinationStopId);
            
            CitizenInfoResponse response = dataCenterProxy.getCitizenInformation(request);
            
            System.out.println("[DataCenterClient] Response received:");
            System.out.println("  Message: " + response.message);
            System.out.println("  From cache: " + response.fromCache);
            
            return response;
            
        } catch (Exception e) {
            System.err.println("[DataCenterClient] Query failed: " + e.getMessage());
            
            // Return error response
            CitizenInfoResponse errorResponse = new CitizenInfoResponse();
            errorResponse.message = "Error querying DataCenter: " + e.getMessage();
            errorResponse.fromCache = false;
            errorResponse.timestamp = System.currentTimeMillis();
            return errorResponse;
        }
    }
    
    /**
     * Submit travel time statistics to DataCenter.
     * Forwards data from OperationControl via ProxyServer to DataCenter.
     */
    public void submitTravelTime(SITM.TravelTimeSubmission submission) {
        try {
            System.out.println("[DataCenterClient] Submitting travel time to DataCenter");
            dataCenterProxy.submitTravelTime(submission);
            System.out.println("[DataCenterClient] Travel time submitted successfully");
        } catch (Exception e) {
            System.err.println("[DataCenterClient] Error submitting travel time: " + e.getMessage());
            throw new RuntimeException("Failed to submit travel time", e);
        }
    }
    
    /**
     * Subscribe to enriched datagram events from DataCenter.
     */
    public void subscribeToEvents() {
        try {
            System.out.println("[DataCenterClient] Subscribing to enriched datagram events...");
            
            // Add subscriber servant to adapter
            ObjectPrx subscriberObj = adapter.add(subscriberServant, Util.stringToIdentity("DataCenterEventSubscriber"));
            DataCenterEventSubscriberPrx subscriberPrx = DataCenterEventSubscriberPrx.uncheckedCast(subscriberObj);
            
            // Activate adapter
            adapter.activate();
            System.out.println("[DataCenterClient] Event adapter activated");
            
            // Subscribe to DataCenter events
            eventPublisherProxy.subscribe(subscriberPrx);
            
            System.out.println("[DataCenterClient] Successfully subscribed to DataCenter events");
            
        } catch (Exception e) {
            System.err.println("[DataCenterClient] Subscription failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Unsubscribe from events and cleanup.
     */
    public void shutdown() {
        try {
            if (eventPublisherProxy != null && subscriberServant != null) {
                System.out.println("[DataCenterClient] Unsubscribing from events...");
                // Note: Need to get subscriber proxy to unsubscribe
                // For now, just destroy communicator which will close connections
            }
            
            if (communicator != null) {
                System.out.println("[DataCenterClient] Destroying ICE communicator...");
                communicator.destroy();
            }
            
            System.out.println("[DataCenterClient] Shutdown complete");
            
        } catch (Exception e) {
            System.err.println("[DataCenterClient] Shutdown error: " + e.getMessage());
        }
    }
    
    /**
     * Get the DataCenter proxy for direct access.
     * 
     * @return DataCenter proxy
     */
    public DataCenterPrx getDataCenterProxy() {
        return dataCenterProxy;
    }
}
