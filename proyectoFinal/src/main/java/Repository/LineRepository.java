package Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DatabaseManager;
import model.Line;

public class LineRepository {

    private final DatabaseManager dbManager;

   
    private static final String BASE_QUERY =
            "SELECT lineid, shortname, description " +
            "FROM mio.lines " +         
            "ORDER BY lineid";

    public LineRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<Line> findAll() throws SQLException {
        List<Line> lines = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(BASE_QUERY);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("lineid");
                String shortName = rs.getString("shortname");
                String description = rs.getString("description");
                lines.add(new Line(id, shortName, description));
            }
        }

        return lines;
    }
}
