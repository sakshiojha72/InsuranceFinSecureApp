package com.ds.app.entity;

import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class Employee extends AppUser {
	
	


    private String employeeCode;  
    private String firstName;
    private String lastName;
    private String email;

   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @JsonIgnoreProperties({"employees", "departments", "projects",
                            "hibernateLazyInitializer", "handler"})
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties({"employees", "projects", "company",
                            "hibernateLazyInitializer", "handler"})
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @JsonIgnoreProperties({"assignedEmployees", "company", "department",
                            "hibernateLazyInitializer", "handler"})
    private Project project;
    
    

    @Enumerated(EnumType.STRING)
    private EmployeeExperience employeeExperience;  // FRESHER / EXPERIENCED 
    
    
    
    @Enumerated(EnumType.STRING)
    private CertificationStatus certificationStatus;
    
    
  
    private Boolean isEscalated = Boolean.FALSE;  

    
    private Double currentSalary = 0.0;
    private LocalDate joiningDate;

    
    
    @Enumerated(EnumType.STRING)
    private Status status=Status.ACTIVE;   // ACTIVE / INACTIVE / TERMINATED
    
    
    
    
    private Boolean isDeleted = Boolean.FALSE;

    //  @OneToMany back-references 
    // cascade = MERGE only —  using ALL (would delete escalations if employee deleted)
    // @JsonIgnore stops loop: Employee → Escalation.targetEmployee → Employee → ...
    
    
    @OneToMany(mappedBy = "targetEmployee", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Escalation> escalations = new ArrayList<>();
    
    

    @OneToMany(mappedBy = "employee", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Appraisal> appraisals = new ArrayList<>();
    
    

    // convenience getters read plain IDs without loading the object
    public Long getCompanyId()    { return company    != null ? company.getId()    : null; }
    public Long getDepartmentId() { return department != null ? department.getId() : null; }
    public Long getProjectId()    { return project    != null ? project.getId()    : null; }
}
