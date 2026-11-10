package ua.edu.nupp.kpp.server;

public class UsageRecord {
    private Long id;
    private long bookingId;
    private String description;
    private String recordedDate;

    public UsageRecord() {
    }

    public UsageRecord(Long id, long bookingId, String description, String recordedDate) {
        this.id = id;
        this.bookingId = bookingId;
        this.description = description;
        this.recordedDate = recordedDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(String recordedDate) {
        this.recordedDate = recordedDate;
    }
}