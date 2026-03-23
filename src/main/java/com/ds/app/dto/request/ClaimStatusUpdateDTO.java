package com.ds.app.dto.request;

import com.ds.app.entity.ClaimStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimStatusUpdateDTO {

    @NotNull(message = "Claim ID is required")
    private Long claimId;

    @NotNull(message = "Status is required")
    private ClaimStatus status; 

    @NotBlank(message = "Admin remarks are required")
    private String adminRemarks;

    private String resolvedBy; 
}