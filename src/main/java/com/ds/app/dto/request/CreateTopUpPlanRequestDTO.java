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
public class CreateTopUpPlanRequestDTO {

    @NotBlank(message = "Top-up name is required")
    private String topUpName;

    @NotNull(message = "Additional coverage is required")
    @Min(value = 1, message = "Additional coverage must be greater than 0")
    private Double additionalCoverage;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;

    private String description;

}