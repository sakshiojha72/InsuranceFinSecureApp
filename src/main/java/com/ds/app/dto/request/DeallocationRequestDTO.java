package com.ds.app.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

// Used by HR to remove an employee from a project, department or company
@Data
public class DeallocationRequestDTO {
	@NotNull(message="Employee user ID is required")
    private Long employeeUserId;  // userId of the employee to deallocate

    // What to remove:
    // PROJECT    → sets project = null only
    // DEPARTMENT → sets department = null + project = null
    // FULL       → sets company = null + department = null + project = null
	@NotNull(message="Deallocation type ID is required")
	@Pattern(regexp = "PROJECT|DEPARTMENT|FULL",message = "Type must be PROJECT or DEPARTMENT or FULL")
    private String type;
}

