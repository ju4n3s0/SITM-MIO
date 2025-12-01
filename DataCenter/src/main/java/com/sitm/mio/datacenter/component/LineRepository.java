package com.sitm.mio.datacenter.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sitm.mio.datacenter.config.ManageDatabase;
import com.sitm.mio.datacenter.interfaces.ILineRepository;
import com.sitm.mio.datacenter.model.Line;

/**
 * Repository for bus line data access.
 * Component from deployment diagram: LineRepository
 * 
 * Realizes: ILineRepository
 */
public class LineRepository implements ILineRepository {
    private static final String TABLE_NAME = "mio.lines";

    private static final String SQL_FIND_BY_ID =
        "SELECT lineid, shortname, description " +
        "FROM " + TABLE_NAME + " WHERE lineid = ?";

    private static final String SQL_FIND_ALL =
        "SELECT lineid, shortname, description " +
        "FROM " + TABLE_NAME;

    private static final String SQL_INSERT =
        "INSERT INTO " + TABLE_NAME +
        " (lineid, shortname, description) " +
        "VALUES (?, ?, ?)";

    private static final String SQL_DELETE =
        "DELETE FROM " + TABLE_NAME + " WHERE lineid = ?";

    
    private Line mapRowToLine(ResultSet rs) throws SQLException {
        long id = rs.getLong("lineid");
        String shortName = rs.getString("shortname");
        String description = rs.getString("description");
        return new Line(id, shortName, description);
    }

    @Override
    public Line findById(Long lineId) {
        try (Connection con = ManageDatabase.gConnection();
                PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setLong(1, lineId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLine(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying line by id " + lineId, e);
        }
        return null;
    }
    
    @Override
    public List<Line> findAll() {
        List<Line> result = new ArrayList<>();

        try (Connection con = ManageDatabase.gConnection();
                PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRowToLine(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying all lines", e);
        }

        return result;
    }
    
    @Override
    public List<Line> findByZone(String zoneId) {
        throw new UnsupportedOperationException(
            "findByZone(String) is not implemented for LineRepository. " +
            "Zones are currently resolved using stops (StopRepository + ZoneUtils)."
        );
    }
    
    @Override
    public void save(Line line) {
        try (Connection con = ManageDatabase.gConnection();
                PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {

            ps.setLong(1, line.getId());
            ps.setString(2, line.getShortName());
            ps.setString(3, line.getDescription());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting line " + line, e);
        }
    }
    
    @Override
    public void delete(Long lineId) {
        try (Connection con = ManageDatabase.gConnection();
                PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, lineId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting line id " + lineId, e);
        }
    }
}
