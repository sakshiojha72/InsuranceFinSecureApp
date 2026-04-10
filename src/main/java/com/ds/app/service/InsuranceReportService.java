package com.ds.app.service;

import com.ds.app.dto.response.ClaimResponseDTO;
import com.ds.app.dto.response.EmployeeInsuranceResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface InsuranceReportService {

    // Query 1 — employees with active insurance AND active top-up
	public List<EmployeeInsuranceResponseDTO> getEmployeesWithInsuranceAndTopUp(int page, int size);

    // Query 2 — employees with active insurance but NO top-up
	public List<EmployeeInsuranceResponseDTO> getEmployeesWithNoTopUp();

    // Query 3a — insurances assigned between two custom dates
	public List<EmployeeInsuranceResponseDTO> getInsurancesAssignedBetween(LocalDate startDate, LocalDate endDate);

    // Query 3b — insurances assigned in the current Indian financial year
	public List<EmployeeInsuranceResponseDTO> getInsurancesForCurrentFinancialYear();

    // Query 4 — all pending claims
	public List<ClaimResponseDTO> getPendingClaims(int page, int size);

    // Query 5 — insurances expiring within the next N days
	public List<EmployeeInsuranceResponseDTO> getInsurancesExpiringSoon(int days);
}