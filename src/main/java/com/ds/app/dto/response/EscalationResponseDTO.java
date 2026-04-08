package com.ds.app.dto.response;

import com.ds.app.enums.EscalationStatus;
import lombok.Data;

import java.time.LocalDateTime;

// Returned by API for escalation data
// HR / Admin / Manager see all fields
// Employee sees only: isEscalated flag + comment + status (handled in service)
@Data
public class EscalationResponseDTO {

    private Long id;

    // plain IDs — who raised and who is targeted
    private Long raisedByUserId;
    private Long targetEmployeeUserId;

    // snapshot context
    private Long departmentId;
    private Long companyId;
    
    
    private String raisedByName; 
    private String targetEmployeeName;
    
    private String comment;

    private EscalationStatus status;

    private LocalDateTime raisedAt;
    private LocalDateTime resolvedAt;  // null until resolved
}
