package com.ds.app.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetDefaultPlanRequestDTO {

    @NotNull(message = "Plan ID is required")
    private Long planId;
}