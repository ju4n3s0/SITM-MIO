package com.sitm.mio.datacenter.interfaces;

import java.util.List;

/**
 * Interface for bus line data access.
 * Realized by: LineRepository
 */
public interface ILineRepository {
    Object findById(Long lineId);
    List<Object> findAll();
    List<Object> findByZone(String zoneId);
    void save(Object line);
    void delete(Long lineId);
}
