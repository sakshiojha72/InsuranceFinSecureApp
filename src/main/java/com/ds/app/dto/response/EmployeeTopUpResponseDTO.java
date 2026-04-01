package com.ds.app.dto.response;

import com.ds.app.entity.InsuranceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTopUpResponseDTO {

    private Long employeeTopUpId;
    private Long employeeId;
    private String employeeName;
    private String topUpName;
    private Double additionalCoverage;
    private Double price;
    private LocalDate purchasedDate;
    private LocalDate expiryDate;
    private InsuranceStatus status;
    private LocalDateTime createdAt;
}