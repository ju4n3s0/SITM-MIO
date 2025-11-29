package com.sitm.mio.datacenter.interfaces;

import java.util.List;

/**
 * Interface for stop/station data access.
 * Realized by: StopRepository
 */
public interface IStopRepository {
    Object findById(Long stopId);
    List<Object> findAll();
    List<Object> findByZone(String zoneId);
    void save(Object stop);
    void delete(Long stopId);
}
