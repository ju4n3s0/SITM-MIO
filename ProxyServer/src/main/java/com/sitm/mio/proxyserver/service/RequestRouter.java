package com.sitm.mio.proxyserver.service;

import SITM.CitizenInformation;
import com.sitm.mio.proxyserver.interfaces.IDataCenterService;
import com.sitm.mio.proxyserver.datacenter.DataCenterClient;
import com.sitm.mio.proxyserver.cache.CacheManager;
import com.sitm.mio.proxyserver.cache.CacheType;

/**
 * Request router that handles requests from citizens.
 * Routes requests through cache and delegates to DataCenter when needed.
 */
public class RequestRouter {

    private final CacheManager cacheManager;
    private final IDataCenterService dataCenter;

    public RequestRouter() {
        this.cacheManager = new CacheManager();
        this.dataCenter = new DataCenterClient();
    }

    public CitizenInformation getCitizenInformation(long originId, long destinationId) {
        String key = originId + "-" + destinationId;
        
        // Check cache first
        CitizenInformation info = cacheManager.get(key);
        if (info != null) {
            System.out.println("Cache HIT for key: " + key);
            return info;
        }
        
        System.out.println("Cache MISS for key: " + key);
        
        // Query DataCenter (network call to remote service)
        CitizenInformation dcInfo = dataCenter.getCitizenInformation(originId, destinationId);
        
        if (dcInfo != null) {
            // Store in cache for future requests
            cacheManager.put(key, dcInfo, CacheType.CITIZEN);
        }
        
        return dcInfo;
    }
    
    /**
     * Get the cache manager instance.
     * Used by AnalyticsService to collect cache statistics.
     */
    public CacheManager getCacheManager() {
        return cacheManager;
    }
}