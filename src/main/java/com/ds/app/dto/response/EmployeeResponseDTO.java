package com.ds.app.dto.response;

import com.ds.app.enums.EmployeeType;
import lombok.Data;

import java.time.LocalDate;

// Returned by API for employee data — plain fields only, no nested objects
@Data
public class EmployeeResponseDTO {

    // from AppUser
    private Integer userId;
    private String username;
    private String role;

    // HR fields
    private String employeeCode;
    private EmployeeType employeeType;

    // plain IDs — caller can fetch details separately if needed
    private Long companyId;
    private Long departmentId;
    private Long projectId;

    private Boolean isCertified;
    private Boolean isEscalated;

    private Double salary;
    private LocalDate joiningDate;
    private String status;
}

