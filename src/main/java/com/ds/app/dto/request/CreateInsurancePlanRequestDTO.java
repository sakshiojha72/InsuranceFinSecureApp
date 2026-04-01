package com.ds.app.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInsurancePlanRequestDTO {

    @NotBlank(message = "Plan name is required")
    private String planName;

    @NotNull(message = "Coverage amount is required")
    @Min(value = 1, message = "Coverage amount must be greater than 0")
    private Double coverageAmount;

    private String description;

}