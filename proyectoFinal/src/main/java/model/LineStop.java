package model;


public class LineStop {

    private final long lineId;
    private final long stopId;
    private final int sequence;       
    private final String orientation; 

    public LineStop(long lineId, long stopId, int sequence, String orientation) {
        this.lineId = lineId;
        this.stopId = stopId;
        this.sequence = sequence;
        this.orientation = orientation;
    }

    public long getLineId() {
        return lineId;
    }

    public long getStopId() {
        return stopId;
    }

    public int getSequence() {
        return sequence;
    }

    public String getOrientation() {
        return orientation;
    }

    @Override
    public String toString() {
        return "LineStop{" +
                "lineId=" + lineId +
                ", stopId=" + stopId +
                ", sequence=" + sequence +
                ", orientation='" + orientation + '\'' +
                '}';
    }
}
