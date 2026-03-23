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
public class BuyTopUpRequestDTO {

    @NotNull(message = "Top-up plan ID is required")
    private Long topUpPlanId;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be a future date")
    private LocalDate expiryDate;
}