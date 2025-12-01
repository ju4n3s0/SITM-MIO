package com.sitm.mio.proxyserver.ice;

import com.zeroc.Ice.Current;
import SITM.*;
import com.sitm.mio.proxyserver.service.RequestRouter;

/**
 * ICE servant implementation for ProxyServer interface.
 * Handles RPC calls from Citizen module.
 */
public class ProxyServerI implements ProxyServer {
    
    private final RequestRouter requestRouter;
    
    public ProxyServerI(RequestRouter requestRouter) {
        this.requestRouter = requestRouter;
        System.out.println("ProxyServerI servant created");
    }
    
    @Override
    public CitizenInformation getCitizenInformation(long originId, long destinationId, Current current) {
        System.out.println("ICE RPC: getCitizenInformation(" + originId + ", " + destinationId + ")");
        
        // Process through RequestRouter (uses cache + DataCenter)
        // RequestRouter now returns ICE CitizenInformation directly
        return requestRouter.getCitizenInformation(originId, destinationId);
    }
}
