package com.ds.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String status = "ACTIVE";   // ACTIVE / COMPLETED / ON_HOLD
    private LocalDate startDate;
    private LocalDate endDate;          // null means ongoing

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @JsonIgnoreProperties({"departments", "projects", "employees",
                            "hibernateLazyInitializer", "handler"})
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties({"employees", "projects", "company",
                            "hibernateLazyInitializer", "handler"})
    private Department department;

    
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Employee> assignedEmployees = new ArrayList<>();

    // convenience getters
    public Long getCompanyId()    { return company    != null ? company.getId()    : null; }
    public Long getDepartmentId() { return department != null ? department.getId() : null; }
}
