package com.ds.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

// Used by HR to create or update a department
@Data
public class DepartmentRequestDTO {
	@NotNull(message="Department name  is required")
    private String name;
	
	@NotBlank(message="Department code is required")
    private String code;
	
	@NotNull(message="Company ID is required")
    private Long companyId;   // which company this department belongs to

	@Pattern(regexp = "ACTIVE|INACTIVE",message="Status must be ACTIVE or INACTIVE")
    private String status;    // ACTIVE / INACTIVE
}

