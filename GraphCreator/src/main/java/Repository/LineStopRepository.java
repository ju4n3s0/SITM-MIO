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

    private static final String qry =
            "SELECT lineid, linevariant, stopid, stopsequence, orientation " +
            "FROM mio.linestops " +
            "ORDER BY lineid, linevariant, orientation, stopsequence";

    public LineStopRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<LineStop> findAll() throws SQLException {
        List<LineStop> result = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(qry);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long lineId = rs.getLong("lineid");
                int linevariant = rs.getInt("linevariant");
                long stopId = rs.getLong("stopid");
                int sequence = rs.getInt("stopsequence");
                int orientation = rs.getInt("Orientation"); //0 or 1 
                result.add(new LineStop(lineId, stopId, sequence, orientation,linevariant));
            }
        }

        return result;
    }
}
