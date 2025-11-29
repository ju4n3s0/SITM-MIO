package com.sitm.mio.datacenter.component;

import com.sitm.mio.datacenter.interfaces.IStopRepository;
import java.util.List;

/**
 * Repository for bus stop/station data access.
 * Component from deployment diagram: StopRepository
 * 
 * Realizes: IStopRepository
 */
public class StopRepository implements IStopRepository {
    
    @Override
    public Object findById(Long stopId) {
        // TODO: Query PostgreSQL for stop by ID
        System.out.println("Finding stop by ID: " + stopId);
        return null;
    }
    
    @Override
    public List<Object> findAll() {
        // TODO: Query all stops
        return List.of();
    }
    
    @Override
    public List<Object> findByZone(String zoneId) {
        // TODO: Query stops by zone
        System.out.println("Finding stops in zone: " + zoneId);
        return List.of();
    }
    
    @Override
    public void save(Object stop) {
        // TODO: Save/update stop
        System.out.println("Saving stop");
    }
    
    @Override
    public void delete(Long stopId) {
        // TODO: Delete stop
        System.out.println("Deleting stop: " + stopId);
    }
}
