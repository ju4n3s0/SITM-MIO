package com.sitm.mio.datacenter.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.sitm.mio.datacenter.config.ManageDatabase;
import com.sitm.mio.datacenter.interfaces.IAuthenticator;
import com.sitm.mio.datacenter.interfaces.IDataCenterFacade;
import com.sitm.mio.datacenter.interfaces.ILineRepository;
import com.sitm.mio.datacenter.interfaces.IStopRepository;
import com.sitm.mio.datacenter.model.DatagramHistoryRecord;
import com.sitm.mio.datacenter.model.Line;
import com.sitm.mio.datacenter.model.Stop;
import com.sitm.mio.datacenter.model.SystemStatistics;
import com.sitm.mio.datacenter.model.ZoneStatistics;

/**
 * Facade providing simplified external access to DataCenter.
 * Component from deployment diagram: DataCenterFacade
 * 
 * Realizes: IDataCenterFacade
 * Uses: IAuthenticator, IStopRepository, ILineRepository
 */
public class DataCenterFacade implements IDataCenterFacade {
    
    private final IAuthenticator authenticator;
    private final IStopRepository stopRepository;
    private final ILineRepository lineRepository;
    
    public DataCenterFacade(IAuthenticator authenticator, 
                            IStopRepository stopRepository,
                            ILineRepository lineRepository) {
        this.authenticator = authenticator;
        this.stopRepository = stopRepository;
        this.lineRepository = lineRepository;
    }
    
    @Override
    public Object getSystemStatistics() {

        System.out.println("[DataCenterFacade] Getting system statistics");

        List<Stop> stops = stopRepository.findAll();
        List<Line> lines = lineRepository.findAll();

        int totalStops = (stops != null) ? stops.size() : 0;
        int totalLines = (lines != null) ? lines.size() : 0;

        return new SystemStatistics(totalStops, totalLines);

    }
    
    @Override
    public Object getZoneStatistics(String zoneId) {
        System.out.println("[DataCenterFacade] Getting zone statistics");

        List<Stop> stopsInZone = stopRepository.findByZone(zoneId);
        int stopCount = (stopsInZone != null) ? stopsInZone.size() : 0;

        return new ZoneStatistics(zoneId, stopCount);

    }
    
    @Override
    public Object getHistoricalData(String timeRange) {
        System.out.println("[DataCenterFacade] Getting historical data");

        //We don't have to make the filtered
        final String SQL = 
        "SELECT raw_id, event_date_txt, bus_id, line_id, gps_x, gps_y, " +
        "       field7, field8, field9, datagram_id, event_ts, field12 " +
        "FROM mio.datagrams_history " +
        "ORDER BY event_ts " +
        "LIMIT 100";
        
        List<DatagramHistoryRecord> result = new ArrayList<>();

        try (Connection con = ManageDatabase.gConnection();
                PreparedStatement ps = con.prepareStatement(SQL);
                ResultSet rs = ps.executeQuery();){

                    while (rs.next()) { 
                        result.add(mapRowToDatagramHistoryRecord(rs));
                    }
            
        } catch (Exception e) {
            throw new RuntimeException("Error getting the historical data", e);
        }
        return result;
    }

    private DatagramHistoryRecord mapRowToDatagramHistoryRecord(ResultSet rs)
            throws Exception {

        long rawId = rs.getLong("raw_id");
        String eventDateTxt = rs.getString("event_date_txt");
        long busId = rs.getLong("bus_id");
        int lineId = rs.getInt("line_id");
        long gpsX = rs.getLong("gps_x");
        long gpsY = rs.getLong("gps_y");
        int field7 = rs.getInt("field7");
        int field8 = rs.getInt("field8");
        int field9 = rs.getInt("field9");
        long datagramId = rs.getLong("datagram_id");
        Timestamp ts = rs.getTimestamp("event_ts");
        Instant eventTs = (ts != null) ? ts.toInstant() : Instant.EPOCH;
        int field12 = rs.getInt("field12");

        return new DatagramHistoryRecord(
                rawId, eventDateTxt, busId, lineId, gpsX, gpsY,
                field7, field8, field9, datagramId, eventTs, field12
        );
    } 
    
    @Override
    public Object authenticateOperator(String username, String password) {
        System.out.println("[DataCenterFacade] Authentication Operator");
        return authenticator.authenticateOperator(username, password);
    }
}
