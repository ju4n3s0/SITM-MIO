package model;


public class LineStop {

    private final long lineId;
    private final long stopId;
    private final int sequence;
    private final int orientation;
    private final int linevariant;

    public LineStop(long lineId, long stopId, int sequence, int orientation, int linevariant) {
        this.lineId = lineId;
        this.stopId = stopId;
        this.sequence = sequence;
        this.orientation = orientation;
        this.linevariant = linevariant;
    }

    public int getLinevariant() {
        return linevariant;
    }

    public int setLinevariant(int linevariant) {
        return this.linevariant;
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

    public int getOrientation() {
        return orientation;
    }

    @Override
    public String toString() {
        return "LineStop{" +
                "lineId=" + lineId +
                ", stopId=" + stopId +
                ", sequence=" + sequence +
                ", orientation='" + orientation + '\'' +
                ", linevariant=" + linevariant +
                '}';
    }
}
