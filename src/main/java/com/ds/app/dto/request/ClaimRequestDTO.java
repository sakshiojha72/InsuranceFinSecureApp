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
public class ClaimRequestDTO {

    @NotNull(message = "Employee insurance ID is required")
    private Long employeeInsuranceId;

    @NotNull(message = "Claim amount is required")
    @Min(value = 1, message = "Claim amount must be greater than 0")
    private Double claimAmount;

    @NotBlank(message = "Reason is required")
    private String reason;
}