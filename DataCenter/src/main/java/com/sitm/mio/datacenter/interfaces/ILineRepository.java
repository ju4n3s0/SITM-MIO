package com.sitm.mio.datacenter.interfaces;

import java.util.List;

import com.sitm.mio.datacenter.model.Line;

/**
 * Interface for bus line data access.
 * Realized by: LineRepository
 */
public interface ILineRepository {
    Line findById(Long lineId);
    List<Line> findAll();
    List<Line> findByZone(String zoneId);
    void save(Line line);
    void delete(Long lineId);
}
