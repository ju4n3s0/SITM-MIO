package com.sitm.mio.datacenter.model;

import java.time.Instant;

/**
 * DTO that represents one row from mio.datagrams_history.
 */
public class DatagramHistoryRecord {

    private final long rawId;
    private final String eventDateTxt;
    private final long busId;
    private final int lineId;
    private final long gpsX;
    private final long gpsY;
    private final int field7;
    private final int field8;
    private final int field9;
    private final long datagramId;
    private final Instant eventTs;
    private final int field12;

    public DatagramHistoryRecord(long rawId,
                                String eventDateTxt,
                                long busId,
                                int lineId,
                                long gpsX,
                                long gpsY,
                                int field7,
                                int field8,
                                int field9,
                                long datagramId,
                                Instant eventTs,
                                int field12) {
        this.rawId = rawId;
        this.eventDateTxt = eventDateTxt;
        this.busId = busId;
        this.lineId = lineId;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
        this.field7 = field7;
        this.field8 = field8;
        this.field9 = field9;
        this.datagramId = datagramId;
        this.eventTs = eventTs;
        this.field12 = field12;
    }

    public long getRawId() { return rawId; }
    public String getEventDateTxt() { return eventDateTxt; }
    public long getBusId() { return busId; }
    public int getLineId() { return lineId; }
    public long getGpsX() { return gpsX; }
    public long getGpsY() { return gpsY; }
    public int getField7() { return field7; }
    public int getField8() { return field8; }
    public int getField9() { return field9; }
    public long getDatagramId() { return datagramId; }
    public Instant getEventTs() { return eventTs; }
    public int getField12() { return field12; }

    @Override
    public String toString() {
        return "DatagramHistoryRecord{" +
                "rawId=" + rawId +
                ", eventDateTxt='" + eventDateTxt + '\'' +
                ", busId=" + busId +
                ", lineId=" + lineId +
                ", gpsX=" + gpsX +
                ", gpsY=" + gpsY +
                ", field7=" + field7 +
                ", field8=" + field8 +
                ", field9=" + field9 +
                ", datagramId=" + datagramId +
                ", eventTs=" + eventTs +
                ", field12=" + field12 +
                '}';
    }
}