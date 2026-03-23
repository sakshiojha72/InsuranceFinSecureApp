package com.ds.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"insurances", "topUps", "insuranceClaims"})
@EqualsAndHashCode(exclude = {"insurances", "topUps", "insuranceClaims"})
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "employee_name", nullable = false)
    private String name;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<EmployeeInsurance> insurances;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<EmployeeTopUp> topUps;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<InsuranceClaim> insuranceClaims;
}