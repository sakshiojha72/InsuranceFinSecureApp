package com.ds.app.dto.response;

import lombok.Data;

import java.time.LocalDate;

// Returned by API for appraisal data
// HR sees all fields
// Employee sees only: revisedSalary (handled in service)
@Data
public class AppraisalResponseDTO {

    private Long id;

    private Long employeeUserId;    // who was appraised

    private Long initiatedByHrUserId; // which HR did it

    private Double previousSalary;

    private Double revisedSalary;

    private String remarks;

    private Integer appraisalYear;

    private LocalDate appraisalDate;
}

