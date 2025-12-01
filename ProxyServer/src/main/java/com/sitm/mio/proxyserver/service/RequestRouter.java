package com.sitm.mio.proxyserver.service;

import com.sitm.mio.proxyserver.dto.GetCitizenInformationRequest;
import com.sitm.mio.proxyserver.interfaces.ICacheService;
import com.sitm.mio.proxyserver.interfaces.IDataCenterService;
import com.sitm.mio.proxyserver.dto.CitizenInformation;
import com.sitm.mio.proxyserver.datacenter.DataCenterClient;
import com.sitm.mio.proxyserver.cache.CacheManager;

/**
 * Request router that handles requests from citizens.
 * Routes requests through cache and delegates to DataCenter when needed.
 */
public class RequestRouter implements ICacheService {

    private final CacheManager cacheManager;
    private final IDataCenterService dataCenter;

    public RequestRouter() {
        this.cacheManager = new CacheManager();
        this.dataCenter = new DataCenterClient();
    }

    @Override
    public CitizenInformation getCitizenInformation(GetCitizenInformationRequest request) {
        String key = request.getOriginId() + "-" + request.getDestinationId();
        
        // Check cache first
        CitizenInformation info = cacheManager.get(key);
        if (info != null) {
            System.out.println("Cache HIT for key: " + key);
            return info;
        }
        
        System.out.println("Cache MISS for key: " + key);
        
        // Query DataCenter (network call to remote service)
        CitizenInformation dcInfo = dataCenter.getCitizenInformation(request);
        
        if (dcInfo != null) {
            // Store in cache for future requests
            cacheManager.put(key, dcInfo);
        }
        
        return dcInfo;
    }
}