package com.ds.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopUpPlanResponseDTO {

    private Long topUpPlanId;
    private String topUpName;
    private Double additionalCoverage;
    private Double price;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
}