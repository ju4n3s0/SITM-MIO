package com.sitm.mio.proxyserver.ice;

import com.zeroc.Ice.Current;

import SITM.DataCenterEventSubscriber;
import SITM.EnrichedDatagram;

import com.sitm.mio.proxyserver.cache.CacheManager;
import com.sitm.mio.proxyserver.cache.CacheType;

/**
 * ICE Servant that receives enriched datagrams from DataCenter.
 * Implements the subscriber interface to get real-time bus data.
 */
public class DataCenterEventSubscriberI implements DataCenterEventSubscriber {
    
    private final CacheManager cacheManager;
    
    public DataCenterEventSubscriberI(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    @Override
    public void onEnrichedDatagram(EnrichedDatagram datagram, Current current) {
        System.out.println("[ProxyServer] Received enriched datagram from DataCenter:");
        System.out.println("  Datagram ID: " + datagram.datagramId);
        System.out.println("  Bus ID: " + datagram.busId);
        System.out.println("  Line ID: " + datagram.lineId);
        System.out.println("  Zone: " + datagram.zoneId);
        System.out.println("  Arc: " + datagram.arcId);
        System.out.println("  Location: (" + datagram.latitude + ", " + datagram.longitude + ")");
        System.out.println("  Timestamp: " + datagram.timestamp);
        
        // Store enriched datagram in cache
        String cacheKey = CacheType.SYSTEM_STATS.createKey("datagram_" + datagram.datagramId);
        cacheManager.put(cacheKey, datagram, CacheType.SYSTEM_STATS);
        
        System.out.println("[ProxyServer] Cached enriched datagram with key: " + cacheKey);
    }
}
