package com.ds.app.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Used by HR to initiate a yearly appraisal for an employee
@Data
public class AppraisalRequestDTO {

	@NotNull(message="Employee user ID is required")
    private Long employeeUserId;  // userId of the employee being appraised

	@NotNull(message = "Revised Salary is Required")
	@Positive(message = "Salary must be greater than zero")
    private Double revisedSalary;    // new salary set by HR

	@Size(max=500,message = "Remarks cannot exceed 500 characters")
    private String remarks;          // HR's notes / reason

	@NotNull(message = "Appraisal year is required")
	@Min(value=2000,message="Appraisal year must be 2000 or later")
    private Integer appraisalYear;   // e.g. 2025
}

