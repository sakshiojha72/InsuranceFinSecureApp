package com.ds.app.dto.response;

import com.ds.app.entity.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponseDTO {

    private Long claimId;
    private Long employeeId;
    private String employeeName;
    private Long employeeInsuranceId;
    private String planName;
    private Double claimAmount;
    private String reason;
    private ClaimStatus status;
    private LocalDateTime raisedAt;
    private LocalDateTime resolvedAt; // null if pending
    private String resolvedBy;        // null if pending
    private String adminRemarks;      // null if pending
    private LocalDateTime createdAt;
}