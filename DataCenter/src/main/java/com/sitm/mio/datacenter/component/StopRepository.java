package com.sitm.mio.datacenter.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sitm.mio.datacenter.config.ManageDatabase;
import com.sitm.mio.datacenter.interfaces.IStopRepository;
import com.sitm.mio.datacenter.model.Stop;
import com.sitm.mio.datacenter.utils.ZoneUtils;

/**
 * Repository for bus stop/station data access.
 * Component from deployment diagram: StopRepository
 * 
 * Realizes: IStopRepository
 */

public class StopRepository implements IStopRepository {
    private static final String TABLE_NAME = "mio.stops";

    private static final String SQL_FIND_BY_ID =
            "SELECT stopid, shortname, longname, decimallatitude, decimallongitude " +
            "FROM " + TABLE_NAME + " WHERE stopid = ?";

    private static final String SQL_FIND_ALL =
            "SELECT stopid, shortname, longname, decimallatitude, decimallongitude " +
            "FROM " + TABLE_NAME;

    private static final String SQL_INSERT =
            "INSERT INTO " + TABLE_NAME +
            " (stopid, shortname, longname, decimallatitude, decimallongitude) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_DELETE =
            "DELETE FROM " + TABLE_NAME + " WHERE stopid = ?";

    //This method is for mapping the row of the db to the Stop Object
    private Stop mapRowToStop(ResultSet rs) throws SQLException{
        long id = rs.getLong("stopid");
        String shortName = rs.getString("shortname");
        String longName = rs.getString("longname");
        double latitude = rs.getDouble("decimallatitude");
        double longitude = rs.getDouble("decimallongitude");

        return new Stop(id, shortName, longName, latitude, longitude);
    }

    @Override
    public Stop findById(Long stopId) {
        try (Connection con = ManageDatabase.gConnection();
            PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_ID)){
                
                
                try {
                    ps.setLong(1, stopId);

                    ResultSet rs = ps.executeQuery();

                    if(rs.next()){
                        return mapRowToStop(rs);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(" Error on the query" + e);
                }
            
                return null;

            }catch (Exception e) {
                throw new RuntimeException(" Error on the connection" + e);
        }

    }

    @Override
    public List<Stop> findAll() {
        List<Stop> result = new ArrayList<>();

        try(Connection con = ManageDatabase.gConnection();
            PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
            ResultSet rs = ps.executeQuery()){

                while(rs.next()){
                    result.add(mapRowToStop(rs));
                }
        } catch (Exception e) {
            throw new RuntimeException(" Error on finding" + e);
        }

        return result;
    }

    @Override
    public List<Stop> findByZone(String zoneId) {
        List<Stop> all = findAll();
        List<Stop> filtered = new ArrayList<>();

        for(Stop stop : all){
            String computeZone = ZoneUtils.computeZone(stop.getLatitude(), stop.getLongitude());
            if (zoneId.equals(computeZone)) {
                filtered.add(stop);
            }
        }
        return filtered;
    }

    @Override
    public void save(Stop stop) {
        try(Connection con = ManageDatabase.gConnection()){
            PreparedStatement ps = con.prepareStatement(SQL_INSERT);

            ps.setLong(1, stop.getId());
            ps.setString(2, stop.getShortName());
            ps.setString(3, stop.getLongName());
            ps.setDouble(4, stop.getLatitude());
            ps.setDouble(5, stop.getLongitude());

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(" Error inserting" + e);
        }
    }

    @Override
    public void delete(Long stopId) {
        try(Connection con = ManageDatabase.gConnection()){
            PreparedStatement ps = con.prepareStatement(SQL_DELETE);

            ps.setLong(1, stopId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error on the delete" + e);
        }
    }

}
