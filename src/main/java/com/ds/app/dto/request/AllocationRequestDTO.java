package com.ds.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Used by HR to assign an employee to a company, department and project
@Data
public class AllocationRequestDTO {

	@NotNull(message="Employee user ID required")
    private Long employeeUserId;  // userId of the employee to assign
	
	@NotNull(message="Compnay ID is Required")
    private Long companyId;          // which company
	
	@NotNull(message="Department ID is required")
    private Long departmentId;       // which department

    private Long projectId;          // which project — optional, can be null
}

