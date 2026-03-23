package com.ds.app.dto.response;

import com.ds.app.entity.InsuranceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceSummaryDTO {

    private Long employeeId;
    private String employeeName;

    private Long employeeInsuranceId;
    private String basePlanName;
    private Double baseCoverageAmount;
    private LocalDate expiryDate;
    private InsuranceStatus insuranceStatus;

    private List<EmployeeTopUpResponseDTO> activeTopUps;

    private Double totalCoverageAmount; // base + sum of all active top-up coverage
}
