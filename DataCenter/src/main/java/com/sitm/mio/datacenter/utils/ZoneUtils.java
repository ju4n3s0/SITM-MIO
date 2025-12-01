package com.sitm.mio.datacenter.utils;

//This class is to calculate the logic zones with the GPS coord 4x10 the división
public class ZoneUtils {
    
    //Values that we got from the pgadmin
    private static final double MIN_LAT = 3.29077 ;
    private static final double MAX_LAT = 3.49886333;
    private static final double MIN_LON = -76.58889111;
    private static final double MAX_LON = -76.46328111;

    //This is the division 4x10
    private static final int ROWS = 4;
    private static final int COLUMNS = 10;

    private ZoneUtils(){}

    public static String computeZone(double latitude, double longitude) {
        // clean 
        double lat = clamp(latitude, MIN_LAT, MAX_LAT);
        double lon = clamp(longitude, MIN_LON, MAX_LON);

        // normalize to [0, 1]
        double latNorm = (lat - MIN_LAT) / (MAX_LAT - MIN_LAT);
        double lonNorm = (lon - MIN_LON) / (MAX_LON - MIN_LON);

        int row = (int) (latNorm * ROWS);
        int col = (int) (lonNorm * COLUMNS);

        // this is when is on the edge
        if (row == ROWS) row--;
        if (col == COLUMNS) col--;

        int index = row * COLUMNS + col + 1;
       
        return String.format("Z%02d", index);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
