
package ua.edu.nupp.kpp.server;

public class UsageRecord {

    private Long id;
    private long equipmentId;
    private long bookingId;
    private String recordDate;
    private double v1;
    private double v2;
    private double v3;
    private double utilizationScore;

    public UsageRecord() {
    }

    public UsageRecord(Long id, long equipmentId, long bookingId,
                       String recordDate, double v1, double v2,
                       double v3, double utilizationScore) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.bookingId = bookingId;
        this.recordDate = recordDate;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.utilizationScore = utilizationScore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public double getV1() {
        return v1;
    }

    public void setV1(double v1) {
        this.v1 = v1;
    }

    public double getV2() {
        return v2;
    }

    public void setV2(double v2) {
        this.v2 = v2;
    }

    public double getV3() {
        return v3;
    }

    public void setV3(double v3) {
        this.v3 = v3;
    }

    public double getUtilizationScore() {
        return utilizationScore;
    }

    public void setUtilizationScore(double utilizationScore) {
        this.utilizationScore = utilizationScore;
    }
}
