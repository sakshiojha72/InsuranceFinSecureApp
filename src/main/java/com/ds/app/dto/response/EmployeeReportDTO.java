package com.ds.app.dto.response;

import com.ds.app.enums.EmployeeExperience;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Lightweight employee entry used inside report responses
// Contains enough info to be useful without being too heavy
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeReportDTO {
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String employeeCode;
    private EmployeeExperience employeeExperience;
    private Boolean isEscalated;
    private Double currentSalary;
    private LocalDate joiningDate;
    private String status;
}

