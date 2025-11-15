package model;

public class Line {

    private long id;
    private String shortName;
    private String description;

    public Line(long id, String shortName, String description) {
        this.id = id;
        this.shortName = shortName;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", shortName='" + shortName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
