package datacenter.model;


public class Stop {

    private final long id;
    private final String shortName;
    private final String longName;
    private final double latitude;
    private final double longitude;

    public Stop(long id, String shortName, String longName, double latitude, double longitude) {
        this.id = id;
        this.shortName = shortName;
        this.longName = longName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}