package com.sitm.mio.datacenter.interfaces;

/**
 * Interface for DataCenter facade providing simplified external access.
 * Realized by: DataCenterFacade
 */
public interface IDataCenterFacade {
    Object getSystemStatistics();
    Object getZoneStatistics(String zoneId);
    Object getHistoricalData(String timeRange);
    Object authenticateOperator(String username, String password);
}
