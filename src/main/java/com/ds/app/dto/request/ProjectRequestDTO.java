package com.ds.app.dto.request;

import lombok.Data;

import java.time.LocalDate;

import com.ds.app.enums.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Used by HR to create or update a project
@Data
public class ProjectRequestDTO {

	@NotBlank(message = "Project name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;


    @NotNull(message = "Company ID is required")
    private Long companyId;


    @NotNull(message = "Department ID is required")
    private Long departmentId;


    
    private String status;


    @NotNull(message = "Start date is required")
    private LocalDate startDate;


    // endDate is optional — null means project is ongoing
    private LocalDate endDate;


   
}

