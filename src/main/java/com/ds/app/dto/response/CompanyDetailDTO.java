package com.ds.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Full company-perspective report
// Shows departments → employees + projects → employees
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDetailDTO {
    private String companyName;
    private String departmentName;
    private String employeeName;
    private String projectName; 
    
}

