package com.sitm.mio.datacenter.component;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.sitm.mio.datacenter.config.ManageDatabase;
import com.sitm.mio.datacenter.interfaces.ITravelTimeStatsRepository;
import com.sitm.mio.datacenter.model.TravelTimeStat;

public class TravelTimeStatsRepository implements ITravelTimeStatsRepository {

    private static final String SQL_INSERT =
        "INSERT INTO mio.travel_time_stats " +
        " (zone_id, avg_travel_time, window_start, window_end, created_at) " +
        " VALUES (?, ?, ?, ?, ?)";

    @Override
    public void save(TravelTimeStat stat) {
        try (Connection con = ManageDatabase.gConnection();
                PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setString(1, stat.getZoneId());
            ps.setDouble(2, stat.getAvgTravelTime());
            ps.setTimestamp(3,
                    stat.getWindowStart() != null
                            ? java.sql.Timestamp.from(stat.getWindowStart())
                            : null);
            ps.setTimestamp(4,
                    stat.getWindowEnd() != null
                            ? java.sql.Timestamp.from(stat.getWindowEnd())
                            : null);
            ps.setTimestamp(5,
                    stat.getCreatedAt() != null
                            ? java.sql.Timestamp.from(stat.getCreatedAt())
                            : java.sql.Timestamp.from(java.time.Instant.now()));

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error inserting travel time stat", e);
        }
    }
}