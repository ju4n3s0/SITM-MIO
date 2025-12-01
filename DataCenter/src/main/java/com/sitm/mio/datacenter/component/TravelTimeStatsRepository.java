package com.sitm.mio.datacenter.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.sitm.mio.datacenter.config.ManageDatabase;
import com.sitm.mio.datacenter.interfaces.ITravelTimeStatsRepository;
import com.sitm.mio.datacenter.model.TravelTimeStat;

public class TravelTimeStatsRepository implements ITravelTimeStatsRepository {

    private static final String SQL_INSERT =
        "INSERT INTO mio.travel_time_stats " +
        " (zone_id, origin_stop_id, destination_stop_id, avg_time_minutes, sample_count, last_updated) " +
        " VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_QUERY_TRAVEL_TIMES =
        "SELECT avg_time_minutes FROM mio.travel_time_stats " +
        "WHERE origin_stop_id = ? AND destination_stop_id = ? " +
        "ORDER BY last_updated DESC LIMIT 10";

    @Override
    public void save(TravelTimeStat stat) {
        try (Connection con = ManageDatabase.gConnection();
                PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setString(1, stat.getZoneId());
            ps.setLong(2, stat.getOriginStopId());
            ps.setLong(3, stat.getDestinationStopId());
            ps.setDouble(4, stat.getAvgTimeMinutes());
            ps.setInt(5, stat.getSampleCount());
            ps.setTimestamp(6,
                    stat.getLastUpdated() != null
                            ? java.sql.Timestamp.from(stat.getLastUpdated())
                            : java.sql.Timestamp.from(java.time.Instant.now()));

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error inserting travel time stat", e);
        }
    }
    
    /**
     * Query travel times for a specific route.
     * @param originStopId Origin stop ID
     * @param destinationStopId Destination stop ID
     * @return List of recent average travel times
     */
    public List<Double> queryTravelTimes(long originStopId, long destinationStopId) {
        List<Double> times = new ArrayList<>();
        
        try (Connection con = ManageDatabase.gConnection();
                PreparedStatement ps = con.prepareStatement(SQL_QUERY_TRAVEL_TIMES)) {
            
            ps.setLong(1, originStopId);
            ps.setLong(2, destinationStopId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    times.add(rs.getDouble("avg_time_minutes"));
                }
            }
            
        } catch (Exception e) {
            System.err.println("[TravelTimeStatsRepository] Error querying travel times: " + e.getMessage());
        }
        
        return times;
    }
}