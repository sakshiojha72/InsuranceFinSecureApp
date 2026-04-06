package com.ds.app.service;

import com.ds.app.dto.response.ClaimResponseDTO;
import com.ds.app.dto.response.EmployeeInsuranceResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface InsuranceReportService {

    // Query 1 — employees with active insurance AND active top-up
	List<EmployeeInsuranceResponseDTO> getEmployeesWithInsuranceAndTopUp(int page, int size);

    // Query 2 — employees with active insurance but NO top-up
    List<EmployeeInsuranceResponseDTO> getEmployeesWithNoTopUp();

    // Query 3a — insurances assigned between two custom dates
    List<EmployeeInsuranceResponseDTO> getInsurancesAssignedBetween(LocalDate startDate, LocalDate endDate);

    // Query 3b — insurances assigned in the current Indian financial year
    List<EmployeeInsuranceResponseDTO> getInsurancesForCurrentFinancialYear();

    // Query 4 — all pending claims
    List<ClaimResponseDTO> getPendingClaims(int page, int size);

    // Query 5 — insurances expiring within the next N days
    List<EmployeeInsuranceResponseDTO> getInsurancesExpiringSoon(int days);
}