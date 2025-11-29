package com.sitm.mio.datacenter.interfaces;

import java.util.List;

/**
 * Interface for zone visualization management.
 * Realized by: GestorVisualizaciónZonas
 */
public interface IZoneVisualizationManager {
    Object getZoneBoundaries(String zoneId);
    List<Object> getAllZones();
    Object getZoneMetadata(String zoneId);
}
