package ua.edu.nupp.kpp.server;

public class Equipment {
    private Long id;
    private String inventoryCode;
    private String name;
    private String category;
    private double utilizationPercent;

    public Equipment() {
    }

    public Equipment(Long id, String inventoryCode, String name,
                     String category, double utilizationPercent) {
        this.id = id;
        this.inventoryCode = inventoryCode;
        this.name = name;
        this.category = category;
        this.utilizationPercent = utilizationPercent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getUtilizationPercent() {
        return utilizationPercent;
    }

    public void setUtilizationPercent(double utilizationPercent) {
        this.utilizationPercent = utilizationPercent;
    }
}