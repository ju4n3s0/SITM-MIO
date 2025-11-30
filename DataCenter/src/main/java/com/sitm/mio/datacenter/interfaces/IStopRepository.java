package com.sitm.mio.datacenter.interfaces;

import java.util.List;

import com.sitm.mio.datacenter.model.Stop;


/**
 * Interface for stop/station data access.
 * Realized by: StopRepository
 */
public interface IStopRepository {
    Stop findById(Long stopId);
    List<Stop> findAll();
    List<Stop> findByZone(String zoneId);
    void save(Stop stop);
    void delete(Long stopId);
}
