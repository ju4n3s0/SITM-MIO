package citizen.utils;


public final class GeoUtils {

    // Radio aproximado de la tierra en kilómetros.
    private static final double EARTH_RADIUS_KM = 6371.0088;

    private GeoUtils() {
        // Constructor privado para evitar instanciación
    }

       public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        // Convertir grados a radianes
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.pow(Math.sin(deltaLat / 2.0), 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.pow(Math.sin(deltaLon / 2.0), 2);
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return EARTH_RADIUS_KM * c;
    }
}