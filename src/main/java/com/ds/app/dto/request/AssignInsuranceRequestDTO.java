
package com.ds.app.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignInsuranceRequestDTO {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Plan ID is required")
    private Long planId;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be a future date")
    private LocalDate expiryDate;
}