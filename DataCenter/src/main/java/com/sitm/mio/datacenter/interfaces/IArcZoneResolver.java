package com.sitm.mio.datacenter.interfaces;

/**
 * Interface for resolving GPS coordinates to arcs and zones.
 * Realized by: ResolvedorDeArcosyZonas
 */
public interface IArcZoneResolver {
    Object resolveArcAndZone(double latitude, double longitude);
    String getZoneId(double latitude, double longitude);
    Long getArcId(double latitude, double longitude);
}
