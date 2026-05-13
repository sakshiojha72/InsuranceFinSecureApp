package com.ds.app.service.impl;

import com.ds.app.dto.response.ClaimResponseDTO;
import com.ds.app.dto.response.EmployeeInsuranceResponseDTO;
import com.ds.app.entity.ClaimStatus;
import com.ds.app.entity.InsuranceClaim;
import com.ds.app.entity.InsuranceStatus;
import com.ds.app.entity.EmployeeInsurance;
import com.ds.app.repository.EmployeeInsuranceRepository;
import com.ds.app.repository.InsuranceClaimRepository;
import com.ds.app.service.InsuranceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class InsuranceReportServiceImpl implements InsuranceReportService {

    @Autowired
    private EmployeeInsuranceRepository employeeInsuranceRepository;

    @Autowired
    private InsuranceClaimRepository insuranceClaimRepository;

    // Reusable list of "valid" statuses — ACTIVE + EXPIRING_SOON
    // EXPIRED employees should not appear in any live report
    private static final List<InsuranceStatus> LIVE_STATUSES =
            Arrays.asList(InsuranceStatus.ACTIVE, InsuranceStatus.EXPIRING_SOON);


    // QUERY 1 — with-topup report
    // Employees who have ACTIVE or EXPIRING_SOON insurance AND at least one top-up
    @Override
    public List<EmployeeInsuranceResponseDTO> getEmployeesWithInsuranceAndTopUp(int page, int size) {

        List<EmployeeInsurance> allRecords = employeeInsuranceRepository
                .findEmployeesWithActiveInsuranceAndTopUp(LIVE_STATUSES);

        int fromIndex = page * size;
        if (fromIndex >= allRecords.size()) return new ArrayList<>();
        int toIndex = Math.min(fromIndex + size, allRecords.size());

        List<EmployeeInsuranceResponseDTO> result = new ArrayList<>();
        for (EmployeeInsurance ins : allRecords.subList(fromIndex, toIndex)) {
            result.add(mapToInsuranceResponse(ins));
        }
        return result;
    }


    // QUERY 2 — no-topup report
    // Employees with ACTIVE or EXPIRING_SOON insurance but NO top-up purchased
    @Override
    public List<EmployeeInsuranceResponseDTO> getEmployeesWithNoTopUp() {

        // Pass LIVE_STATUSES so EXPIRING_SOON employees without top-ups are included
        List<EmployeeInsurance> records = employeeInsuranceRepository
                .findEmployeesWithInsuranceButNoTopUp(LIVE_STATUSES);

        List<EmployeeInsuranceResponseDTO> result = new ArrayList<>();
        for (EmployeeInsurance ins : records) {
            result.add(mapToInsuranceResponse(ins));
        }
        return result;
    }


    // QUERY 3a — assigned-between report
    // No status filter in the query — all assignments in date range are shown
    @Override
    public List<EmployeeInsuranceResponseDTO> getInsurancesAssignedBetween(
            LocalDate startDate, LocalDate endDate) {

        List<EmployeeInsurance> records = employeeInsuranceRepository
                .findInsurancesAssignedBetween(startDate, endDate);
        // NOTE: only 2 params now — status filter removed from repo query

        List<EmployeeInsuranceResponseDTO> result = new ArrayList<>();
        for (EmployeeInsurance ins : records) {
            result.add(mapToInsuranceResponse(ins));
        }
        return result;
    }


    // QUERY 3b — current financial year report
    // Indian FY: April 1 to March 31
    // If month >= 4 (Apr-Dec), FY started this year. If Jan-Mar, FY started last year.
    @Override
    public List<EmployeeInsuranceResponseDTO> getInsurancesForCurrentFinancialYear() {

        LocalDate today = LocalDate.now();
        int fyStartYear = (today.getMonthValue() >= 4) ? today.getYear() : today.getYear() - 1;
        LocalDate startDate = LocalDate.of(fyStartYear, 4, 1);
        LocalDate endDate   = LocalDate.of(fyStartYear + 1, 3, 31);

        return getInsurancesAssignedBetween(startDate, endDate);
    }


    // QUERY 4 — pending claims report
    @Override
    public List<ClaimResponseDTO> getPendingClaims(int page, int size) {

        List<InsuranceClaim> allRecords = insuranceClaimRepository
                .findAllPendingClaims(ClaimStatus.PENDING);

        int fromIndex = page * size;
        if (fromIndex >= allRecords.size()) return new ArrayList<>();
        int toIndex = Math.min(fromIndex + size, allRecords.size());

        List<ClaimResponseDTO> result = new ArrayList<>();
        for (InsuranceClaim claim : allRecords.subList(fromIndex, toIndex)) {
            result.add(mapToClaimResponse(claim));
        }
        return result;
    }


    // QUERY 5 — expiring-soon report
    // Show ACTIVE and EXPIRING_SOON policies expiring within N days
    @Override
    public List<EmployeeInsuranceResponseDTO> getInsurancesExpiringSoon(int days) {

        LocalDate today     = LocalDate.now();
        LocalDate alertDate = today.plusDays(days);

        // Pass LIVE_STATUSES — EXPIRING_SOON records must show up here
        List<EmployeeInsurance> records = employeeInsuranceRepository
                .findInsurancesExpiringBetween(LIVE_STATUSES, today, alertDate);

        List<EmployeeInsuranceResponseDTO> result = new ArrayList<>();
        for (EmployeeInsurance ins : records) {
            result.add(mapToInsuranceResponse(ins));
        }
        return result;
    }


    // ─── MAPPERS ──────────────────────────────────────────────────────────────

    private EmployeeInsuranceResponseDTO mapToInsuranceResponse(EmployeeInsurance ins) {
        EmployeeInsuranceResponseDTO dto = new EmployeeInsuranceResponseDTO();
        dto.setEmployeeInsuranceId(ins.getId());
        dto.setEmployeeId(ins.getEmployee().getUserId());

        String firstName = ins.getEmployee().getFirstName();
        String lastName  = ins.getEmployee().getLastName();
        dto.setEmployeeName(
            (firstName != null && !firstName.isBlank() && lastName != null && !lastName.isBlank())
                ? firstName + " " + lastName
                : ins.getEmployee().getUsername()
        );

        dto.setPlanName(ins.getInsurancePlan().getPlanName());
        dto.setCoverageAmount(ins.getInsurancePlan().getCoverageAmount());
        dto.setAssignedDate(ins.getAssignedDate());
        dto.setExpiryDate(ins.getExpiryDate());
        dto.setStatus(ins.getStatus());
        dto.setCreatedAt(ins.getCreatedAt());
        return dto;
    }

    private ClaimResponseDTO mapToClaimResponse(InsuranceClaim claim) {
        ClaimResponseDTO dto = new ClaimResponseDTO();
        dto.setClaimId(claim.getId());
        dto.setEmployeeId(claim.getEmployee().getUserId());

        String firstName = claim.getEmployee().getFirstName();
        String lastName  = claim.getEmployee().getLastName();
        dto.setEmployeeName(
            (firstName != null && !firstName.isBlank() && lastName != null && !lastName.isBlank())
                ? firstName + " " + lastName
                : claim.getEmployee().getUsername()
        );

        dto.setEmployeeInsuranceId(claim.getEmployeeInsurance().getId());
        dto.setPlanName(claim.getEmployeeInsurance().getInsurancePlan().getPlanName());
        dto.setClaimAmount(claim.getClaimAmount());
        dto.setReason(claim.getReason());
        dto.setStatus(claim.getStatus());
        dto.setRaisedAt(claim.getRaisedAt());
        dto.setResolvedAt(claim.getResolvedAt());
        dto.setResolvedBy(claim.getResolvedBy());
        dto.setAdminRemarks(claim.getAdminRemarks());
        dto.setCreatedAt(claim.getCreatedAt());
        return dto;
    }
}