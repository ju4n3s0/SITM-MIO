package com.sitm.mio.datacenter.interfaces;

import java.util.List;

import com.sitm.mio.datacenter.model.DatagramHistoryRecord;
import com.sitm.mio.datacenter.model.OperatorAuthResult;
import com.sitm.mio.datacenter.model.OperatorCredentials;
import com.sitm.mio.datacenter.model.SystemStatistics;
import com.sitm.mio.datacenter.model.TravelTimeStat;
import com.sitm.mio.datacenter.model.ZoneStatistics;

public interface IServiceDataCenter {

    OperatorAuthResult loginOperator(OperatorCredentials credentials);

    SystemStatistics getSystemStatistics(String token);

    ZoneStatistics getZoneStatistics(String token, String zoneId);
    
    void reportTravelTimeStat(String token, TravelTimeStat stat);

    List<DatagramHistoryRecord> getZoneDatagrams(
            String token,
            String zoneId,
            String timeRange
    );  
}