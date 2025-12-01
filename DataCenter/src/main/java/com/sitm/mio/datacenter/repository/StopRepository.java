package datacenter.repository;

import datacenter.db.DBManager;
import datacenter.model.Stop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Stop;


public class StopRepository {

    private static final String SQL_ALL_STOPS =
            "SELECT stopid, shortname, longname, latitude, longitude FROM mio.stops";

    private static final String SQL_STOP_BY_ID =
            "SELECT stopid, shortname, longname, latitude, longitude FROM mio.stops WHERE stopid = ?";

    
    public List<Stop> findAll() throws SQLException {
        List<Stop> stops = new ArrayList<>();
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_ALL_STOPS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                long id = rs.getLong("stopid");
                String shortName = rs.getString("shortname");
                String longName = rs.getString("longname");
                double lat = rs.getDouble("latitude");
                double lon = rs.getDouble("longitude");
                stops.add(new Stop(id, shortName, longName, lat, lon));
            }
        }
        return stops;
    }

    
    public Stop findById(long id) throws SQLException {
        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_STOP_BY_ID)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String shortName = rs.getString("shortname");
                    String longName = rs.getString("longname");
                    double lat = rs.getDouble("latitude");
                    double lon = rs.getDouble("longitude");
                    return new Stop(id, shortName, longName, lat, lon);
                }
            }
        }
        return null;
    }
}