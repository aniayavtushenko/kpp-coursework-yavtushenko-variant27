package ua.edu.nupp.kpp.server;

public class Booking {

    private Long id;
    private long equipmentId;
    private long researcherId;
    private BookingState state;
    private String deadline; // Змінено з LocalDate на String
    private double f1;
    private double f2;
    private double f3;
    private double riskIndex;

    public Booking() {
    }

    public Booking(Long id, long equipmentId, long researcherId,
                   BookingState state, String deadline,
                   double f1, double f2, double f3, double riskIndex) {

        this.id = id;
        this.equipmentId = equipmentId;
        this.researcherId = researcherId;
        this.state = state;
        this.deadline = deadline;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.riskIndex = riskIndex;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getEquipmentId() { return equipmentId; }
    public void setEquipmentId(long equipmentId) { this.equipmentId = equipmentId; }

    public long getResearcherId() { return researcherId; }
    public void setResearcherId(long researcherId) { this.researcherId = researcherId; }

    public BookingState getState() { return state; }
    public void setState(BookingState state) { this.state = state; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public double getF1() { return f1; }
    public void setF1(double f1) { this.f1 = f1; }

    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }

    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }

    public double getRiskIndex() { return riskIndex; }
    public void setRiskIndex(double riskIndex) { this.riskIndex = riskIndex; }
}