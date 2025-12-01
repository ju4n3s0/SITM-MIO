package com.sitm.mio.proxyserver.interfaces;

import com.sitm.mio.proxyserver.dto.GetCitizenInformationRequest;
import com.sitm.mio.proxyserver.dto.CitizenInformation;

/**
 * Interface for DataCenter service operations.
 * 
 * Note: This is a local interface for ProxyServer.
 * The actual DataCenter runs on a different machine and communicates via network.
 */
public interface IDataCenterService {
    
    /**
     * Get citizen information from DataCenter.
     * @param request The request with origin and destination IDs
     * @return CitizenInformation or null if not available
     */
    CitizenInformation getCitizenInformation(GetCitizenInformationRequest request);
}
