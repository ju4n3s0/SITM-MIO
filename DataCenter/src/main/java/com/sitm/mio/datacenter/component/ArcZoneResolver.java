package com.sitm.mio.datacenter.component;

import java.util.List;

import com.sitm.mio.datacenter.interfaces.IArcZoneResolver;
import com.sitm.mio.datacenter.interfaces.IStopRepository;
import com.sitm.mio.datacenter.model.Stop;

/**
 * Resolves GPS coordinates to arcs and zones.
 * Component from deployment diagram: ResolvedorDeArcosyZonas
 * 
 * Realizes: IArcZoneResolver
 * Uses: IStopRepository
 */
public class ArcZoneResolver implements IArcZoneResolver {
    
    private final IStopRepository stopRepository;
    
    public ArcZoneResolver(IStopRepository stopRepository) {
        this.stopRepository = stopRepository;
    }
    
    @Override
    public Object resolveArcAndZone(double latitude, double longitude) {
        // TODO: Implement geographic resolution
        // 1. Query stopRepository for nearby stops
        // 2. Determine arc based on GPS position
        // 3. Determine zone from arc
        // 4. Return { arcId, zoneId, confidence }

        //Serach the nearby stops
        Stop nearest = findNearestStop(latitude, longitude);

        if (nearest == null) {
            System.out.println("[ArcZoneResolver] Not found that stop: "
                    + latitude + ", " + longitude);
            return null;
        }

        //This is for obtain the zone and the arch
        String zoneId = getZoneId(latitude, longitude);
        Long arcId = getArcId(latitude, longitude);

        class Result {
            Long arc;
            String zone;
            long stopId;

            Result(Long arc, String zone, long stopId) {
                this.arc = arc;
                this.zone = zone;
                this.stopId = stopId;
            }

            @Override
            public String toString() {
                return "Result{arc=" + arc + ", zone='" + zone + "', stopId=" + stopId + "}";
            }
        }

        Result result = new Result(arcId, zoneId, nearest.getId());
        System.out.println("[ArcZoneResolver] " + result);
        return result;
    }
    
    @Override
    public String getZoneId(double latitude, double longitude) {
        Stop nearest = findNearestStop(latitude, longitude);
        if (nearest == null) {
            return null;
        }
        return nearest.getLongName();
    }
    
    @Override
    public Long getArcId(double latitude, double longitude) {
        // TODO: Get arc ID from coordinates
        return null;
    }
    //This method is to seach the nearest stop
    private Stop findNearestStop(double latitude, double longitude){
        List<Stop> rawStops = stopRepository.findAll();

        if (rawStops == null || rawStops.isEmpty()) {
            return null;
        }
        Stop best = null;
        double bestDistance = Double.MAX_VALUE;

        for (Object obj : rawStops) {
            Stop stop = (Stop) obj; // hacemos cast porque el repo devuelve Object
            double d = distanceInMeters(
                    latitude, longitude,
                    stop.getLatitude(), stop.getLongitude()
            );
            if (d < bestDistance) {
                bestDistance = d;
                best = stop;
            }
        }
        return best;
    }

    //This method is for the aprox of distance in meters
    private double distanceInMeters(double lat1, double lon1, double lat2, double lon2){
        double Rearth = 6371000;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Rearth * c;
    }
}
