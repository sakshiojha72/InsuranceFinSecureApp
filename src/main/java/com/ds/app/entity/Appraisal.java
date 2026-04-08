package com.ds.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appraisal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // employee being appraised
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @JsonIgnoreProperties({"escalations", "appraisals", "company", "department", "project",
                            "hibernateLazyInitializer", "handler"})
    private Employee employee;

    // HR who did the appraisal
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_id")
    @JsonIgnoreProperties({"escalations", "appraisals", "company", "department", "project",
                            "hibernateLazyInitializer", "handler"})
    private Employee initiatedByHr;

    private Double previousSalary;      // snapshot before update
    private Double revisedSalary;       // new salary set by HR

    private String remarks;             // HR's reason / notes
    private Integer appraisalYear;      // e.g. 2025
    private LocalDate appraisalDate;    // exact date of appraisal
}
