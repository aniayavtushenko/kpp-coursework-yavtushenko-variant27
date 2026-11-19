package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Researcher {
    private Long id;

    @JsonAlias({"full_name", "fullName", "name"})
    @JsonProperty("full_name")
    private String name;

    @JsonAlias({"department"})
    @JsonProperty("department")
    private String department;

    @JsonAlias({"email"})
    @JsonProperty("email")
    private String email;

    public Researcher() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}