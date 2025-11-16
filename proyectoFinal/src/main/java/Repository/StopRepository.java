package Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import db.DatabaseManager;
import model.Stop;

public class StopRepository {

    private final DatabaseManager dbManager;

    public StopRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    public List<Stop> findAll() throws SQLException {
        List<Stop> stops = new java.util.ArrayList<>();
        String sql = """
                SELECT stopid,
                       shortname,
                       longname,
                       decimallatitude,
                       decimallongitude
                FROM mio.stops
                """;

        try (Connection conn = dbManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    long id = rs.getLong("stopid");
                    String shortName = rs.getString("shortname");
                    String longName = rs.getString("longname");
                    double lat = rs.getDouble("decimallatitude");
                    double lon = rs.getDouble("decimallongitude");
    
                    Stop stop = new Stop(id, shortName, longName, lat, lon);
                    stops.add(stop);
                }
        } catch (Exception e) {
            throw new SQLException("Error fetching all stops", e);
        }
        return stops;
    }

}
