package ua.edu.nupp.kpp.server;

public class Researcher {
    private Long id;
    private String fullName;
    private String department;
    private String email;

    public Researcher() {
    }

    public Researcher(Long id, String fullName, String department, String email) {
        this.id = id;
        this.fullName = fullName;
        this.department = department;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}