package com.ds.app.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Used by Manager / HR / Admin to raise an escalation against an employee
@Data
public class EscalationRequestDTO {
	@NotNull(message="Target Employee user ID is required")
    private Long targetEmployeeUserId;  // userId of the employee being escalated
	
	@NotNull(message="comment is required")
	@Size(min=2,max=500,message="Comment must be between 2 to 500 characters ")
    private String comment;             // reason for escalation
}

