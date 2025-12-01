package com.sitm.mio.datacenter.component;

import java.util.ArrayList;
import java.util.List;

import com.sitm.mio.datacenter.interfaces.IArcZoneResolver;
import com.sitm.mio.datacenter.interfaces.IAuthenticator;
import com.sitm.mio.datacenter.interfaces.IDataCenterFacade;
import com.sitm.mio.datacenter.interfaces.IServiceDataCenter;
import com.sitm.mio.datacenter.model.DatagramHistoryRecord;
import com.sitm.mio.datacenter.model.OperatorAuthResult;
import com.sitm.mio.datacenter.model.OperatorCredentials;
import com.sitm.mio.datacenter.model.SystemStatistics;
import com.sitm.mio.datacenter.model.TravelTimeStat;
import com.sitm.mio.datacenter.model.ZoneStatistics;

public class ServiceDataCenter implements IServiceDataCenter {

    private final IDataCenterFacade facade;
    private final IAuthenticator authenticator;
    private final IArcZoneResolver arcZoneResolver;

    public ServiceDataCenter(IDataCenterFacade facade,
                                IAuthenticator authenticator, IArcZoneResolver arcZoneResolver) {
        this.facade = facade;
        this.authenticator = authenticator;
        this.arcZoneResolver = arcZoneResolver;
    }

    @Override
    public OperatorAuthResult loginOperator(OperatorCredentials credentials) {
        Object result = facade.authenticateOperator(
            credentials.getUsername(), credentials.getPassword());

        return (OperatorAuthResult) result;
    }

    @Override
    public SystemStatistics getSystemStatistics(String token) {

        if (!authenticator.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        return (SystemStatistics) facade.getSystemStatistics();
    }

    @Override
    public ZoneStatistics getZoneStatistics(String token, String zoneId) {

        if (!authenticator.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        return (ZoneStatistics) facade.getZoneStatistics(zoneId);
    }

    @Override
    public List<DatagramHistoryRecord> getZoneDatagrams(String token, String zoneId, String timeRange) {

        OperatorAuthResult session = authenticator.findSessionByToken(token);
        if (!authenticator.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        @SuppressWarnings("unchecked")
        List<DatagramHistoryRecord> all = (List<DatagramHistoryRecord>) facade.getHistoricalData(timeRange);

        List<DatagramHistoryRecord> filtered = new ArrayList<>();

        for (DatagramHistoryRecord r : all) {
            // we convert the cords to double 
            double lat = r.getGpsY() / 1e7;
            double lon = r.getGpsX() / 1e7;
    
            // Calculate the zone
            String z = arcZoneResolver.getZoneId(lat, lon);
    
            if (zoneId.equals(z)) {
                filtered.add(r);
            }
        }
    
        return filtered;
    }

    @Override
    public void reportTravelTimeStat(String token, TravelTimeStat stat) {

        OperatorAuthResult session = authenticator.findSessionByToken(token);

        if (!authenticator.validateToken(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        
        // Note: TravelTimeStat now uses origin/destination stops instead of zones
        // Zone authorization check removed - travel time stats are route-based

        facade.saveTravelTimeStat(stat);
    }
    
    
}
