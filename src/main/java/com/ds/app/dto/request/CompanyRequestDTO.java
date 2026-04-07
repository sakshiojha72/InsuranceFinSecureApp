package com.ds.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyRequestDTO {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Company code is required")
    @Size(max = 20, message = "Code cannot exceed 20 characters")
    private String code;  // e.g. ICICI-001

    private Boolean restrictsInvestment = false;

    @Pattern(regexp = "ACTIVE|INACTIVE",
             message = "Status must be ACTIVE or INACTIVE")
    private String status = "ACTIVE";
}

