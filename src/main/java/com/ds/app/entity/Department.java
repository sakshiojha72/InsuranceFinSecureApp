package com.ds.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String code;
    private String status = "ACTIVE";

    // Department belongs to ONE Company — @ManyToOne (consistent with Project)
    // @JsonIgnoreProperties stops loop: Dept → Company.departments → Dept → ...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @JsonIgnoreProperties({"departments", "projects", "employees",
                            "hibernateLazyInitializer", "handler"})
    private Company company;

    // bidirectional 
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Project> projects = new ArrayList<>();

    // convenience getter — other code can still call dept.getCompanyId()
    public Long getCompanyId() {
        return company != null ? company.getId() : null;
    }
}
