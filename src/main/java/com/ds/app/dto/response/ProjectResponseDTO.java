package com.ds.app.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectResponseDTO {

    private Long id;
    private String name;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;

    // plain IDs + names for readability
    private Long companyId;
    private String companyName;
    private Long departmentId;
    private String departmentName;
}
