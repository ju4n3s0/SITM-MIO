package Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DatabaseManager;
import model.LineStop;

public class LineStopRepository {

    private final DatabaseManager dbManager;

    private static final String BASE_QUERY =
            "SELECT lineid, stopid, stopsequence, orientation " +
            "FROM mio.linestops " +   
            "ORDER BY lineid, orientation, stopsequence";

    public LineStopRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<LineStop> findAll() throws SQLException {
        List<LineStop> result = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(BASE_QUERY);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long lineId = rs.getLong("lineid");
                long stopId = rs.getLong("stopid");
                int sequence = rs.getInt("stopsequence");
                String orientation = rs.getString("orientation");
                result.add(new LineStop(lineId, stopId, sequence, orientation));
            }
        }

        return result;
    }
}
