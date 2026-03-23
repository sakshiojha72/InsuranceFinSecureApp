package com.ds.app.dto.request;

import com.ds.app.enums.EmployeeType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDate;

// Used by HR to create or update an employee profile
@Data
public class EmployeeRequestDTO {

	@NotBlank(message = "Employee Code is Rerquired")
    private String employeeCode;       // e.g. EMP-001
    
	@NotNull(message="Employee code is required")
    private EmployeeType employeeType; // FRESHER / EXPERIENCED / CERTIFIED

    private Boolean isCertified=false;
    
    @PositiveOrZero(message="Salary cannot be negative")
    private Double salary;
    
    @NotNull(message = "Hoining date is Required")
    private LocalDate joiningDate;

    
    @Pattern(regexp = "ACTIVE|INACTIVE|TERMINATED",message ="Status must be ACTIVE|INACTIVE|TERMINATED ")
    private String status;             // ACTIVE / INACTIVE / TERMINATED
}

