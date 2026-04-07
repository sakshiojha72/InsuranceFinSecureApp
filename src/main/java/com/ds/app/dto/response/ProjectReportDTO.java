package com.ds.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

// Project with its list of assigned employees
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectReportDTO {
    private Long projectId;
    private String projectName;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalEmployees;
    private List<EmployeeReportDTO> employees;
}
