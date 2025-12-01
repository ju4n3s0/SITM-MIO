package com.sitm.mio.datacenter.component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sitm.mio.datacenter.config.ManageDatabase;
import com.sitm.mio.datacenter.interfaces.IAuthenticator;
import com.sitm.mio.datacenter.model.OperatorAuthResult;

/**
 * Authentication service for operators and citizens.
 * Component from deployment diagram: Autenticador
 * 
 * Realizes: IAuthenticator
 */
public class Authenticator implements IAuthenticator {
    
    private final Map<String, OperatorAuthResult> sessions = 
        new ConcurrentHashMap<>();

    private static final String SQL_FIND_OPERATOR =
            "SELECT operator_id, username, password_hash " +
            "FROM mio.operators WHERE username = ?";

    private static final String SQL_FIND_ZONES_FOR_OPERATOR =
            "SELECT zone_id FROM mio.operator_zones WHERE operator_id = ?";
    
    @Override
    public Object authenticateOperator(String username, String password) {
        // TODO: Implement operator authentication
        // 1. Query database for operator credentials
        // 2. Verify password hash
        // 3. Fetch assigned zones
        // 4. Return { operatorId, username, assignedZones[] }
        System.out.println("[Authenticator] Authenticating operator: " + username);

        try (Connection con = ManageDatabase.gConnection();
                PreparedStatement ps = con.prepareStatement(SQL_FIND_OPERATOR)){

                    ps.setString(1, username);
                    try (ResultSet rs = ps.executeQuery()){
                        //Validate if the user exist
                        if (!rs.next()) {
                            System.out.println("[Authenticator] Operator not found");
                            return null;
                        }
                        
                        long operatorId = rs.getLong("operator_id");
                        String storedHash = rs.getString("password_hash");

                        //Validate the password
                        if (!storedHash.equals(password)) {
                            System.out.println("[Authenticator] Invalid user or password.");
                            return null;
                        }

                        //Obtain the zones of the operator
                        List<String> zones = loadAssignedZones(con, operatorId);

                        //Generate Token
                        String token = UUID.randomUUID().toString();

                        //Return the DTO
                        OperatorAuthResult session = new OperatorAuthResult(operatorId, username, zones, token);
                        
                        //Save on the map
                        sessions.put(token, session);
                        return session;

                    } catch (Exception e) {
                        throw new RuntimeException("Error authenticating" , e);
                    }
        } catch (Exception e) {
            throw new RuntimeException("Error on the connection", e);
        }
    }

    private List<String> loadAssignedZones(Connection con , long operatorId){
        List<String> zones = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(SQL_FIND_ZONES_FOR_OPERATOR)){
            
            ps.setLong(1, operatorId);

            try (ResultSet rs = ps.executeQuery()){
                
                while (rs.next()) { 
                    zones.add(rs.getString("zone_id"));
                }

            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }
            
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return zones;
    }
    
    @Override
    public Object authenticateCitizen(String credentials) {
        //For the Citizen doesn't need authentication 

        System.out.println("[Authenticatior] Citizen access granted");
        return "OK"; 
    }
    
    @Override
    public boolean validateToken(String token) {

        if (token == null || token.isBlank()) {
            return false;
        }

        return sessions.containsKey(token);
    }

    @Override
    public OperatorAuthResult findSessionByToken(String token) {
        if (token == null) return null;
        return sessions.get(token);
    }

    @Override
    public void removeSession(String token) {
        sessions.remove(token);
    }
}
