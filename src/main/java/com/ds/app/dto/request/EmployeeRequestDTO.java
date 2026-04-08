package com.ds.app.dto.request;

import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;

// Used by HR to create or update an employee profile
@Data
public class EmployeeRequestDTO {

	@NotBlank(message = "Employee Code is Rerquired")
    private String employeeCode;       // e.g. EMP-001
    
	@NotNull(message="Employee Experience status  is required")
    private EmployeeExperience employeeExperience; // FRESHER / EXPERIENCED 
    
	@NotNull(message="EMployee Certification needed")
	private CertificationStatus certificationStatus;
   // private Boolean isCertified=false;
    
    @PositiveOrZero(message="Salary cannot be negative")
    private Double currentSalary;
    
    @NotNull(message = "Hoining date is Required")
    private LocalDate joiningDate;

    
    @NotNull(message ="Status cannot be not Null")
    private Status status;             // ACTIVE / INACTIVE / TERMINATED
}

