package com.ds.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePlanResponseDTO {

    private Long planId;
    private String planName;
    private Double coverageAmount;
    private String description;
    private Boolean isActive;
    private String createdBy;
    private LocalDateTime createdAt;
}