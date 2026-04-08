package com.ds.app.dto.response;

import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.Status;

import lombok.Data;

import java.time.LocalDate;

// Returned by API for employee data — plain fields only, no nested objects
@Data
public class EmployeeResponseDTO {

    // from AppUser
    private Long userId;
    private String username;
    private String role;

    // HR fields
    private String employeeCode;
    private EmployeeExperience employeeExperience;
    
    private CertificationStatus certificationStatus;

    // plain IDs — caller can fetch details separately if needed
    private Long companyId;
    private Long departmentId;
    private Long projectId;

    private Boolean isCertified;
    private Boolean isEscalated;

    private Double currentSalary;
    private LocalDate joiningDate;
    private Status status;
}

