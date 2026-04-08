package com.ds.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Used by HR to create or update a department
@Data
public class DepartmentRequestDTO {
	@NotNull(message="Department name  is required")
	 @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
	
	@NotBlank(message="Department code is required")
	@Size(max = 20, message = "Code cannot exceed 20 characters")

    private String code;
	
	@NotNull(message="Company ID is required")
    private Long companyId;   // which company this department belongs to

	@Pattern(regexp = "ACTIVE|INACTIVE",message="Status must be ACTIVE or INACTIVE")
    private String status;    // ACTIVE / INACTIVE
}

