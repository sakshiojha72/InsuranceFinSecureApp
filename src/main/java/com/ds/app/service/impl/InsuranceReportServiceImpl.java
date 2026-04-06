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
import java.util.List;

@Service
public class InsuranceReportServiceImpl implements InsuranceReportService {

    @Autowired
    private EmployeeInsuranceRepository employeeInsuranceRepository;

    @Autowired
    private InsuranceClaimRepository insuranceClaimRepository;


    // QUERY 1
    // Employees who have active insurance AND at least one active top-up
 // QUERY 1
    @Override
    public List<EmployeeInsuranceResponseDTO> getEmployeesWithInsuranceAndTopUp(int page, int size) {

        List<EmployeeInsurance> allRecords = employeeInsuranceRepository
                .findEmployeesWithActiveInsuranceAndTopUp(
                    InsuranceStatus.ACTIVE,
                    InsuranceStatus.ACTIVE
                );

        // calculate start index
        int fromIndex = page * size;

        // if page is out of range return empty list
        if (fromIndex >= allRecords.size()) {
            return new ArrayList<>();
        }

        // toIndex should not go beyond list size
        int toIndex = fromIndex + size;
        if (toIndex > allRecords.size()) {
            toIndex = allRecords.size();
        }

        // cut the list to just this page
        List<EmployeeInsurance> pageRecords = allRecords.subList(fromIndex, toIndex);

        // map to DTO
        List<EmployeeInsuranceResponseDTO> result = new ArrayList<>();
        for (EmployeeInsurance ins : pageRecords) {
            result.add(mapToInsuranceResponse(ins));
        }
        return result;
    }


    // QUERY 2
    // Employees who have active insurance but NO top-ups at all
    @Override
    public List<EmployeeInsuranceResponseDTO> getEmployeesWithNoTopUp() {

        List<EmployeeInsurance> records = employeeInsuranceRepository
                .findEmployeesWithInsuranceButNoTopUp(
                    InsuranceStatus.ACTIVE    // only check ACTIVE insurances
                );

        List<EmployeeInsuranceResponseDTO> result = new ArrayList<>();
        for (EmployeeInsurance ins : records) {
            result.add(mapToInsuranceResponse(ins));
        }
        return result;
    }


    // QUERY 3a
    // Insurances assigned between any two dates the caller passes
    @Override
    public List<EmployeeInsuranceResponseDTO> getInsurancesAssignedBetween(
            LocalDate startDate,
            LocalDate endDate) {

        List<EmployeeInsurance> records = employeeInsuranceRepository
                .findInsurancesAssignedBetween(
                    startDate,
                    endDate,
                    InsuranceStatus.ACTIVE
                );

        List<EmployeeInsuranceResponseDTO> result = new ArrayList<>();
        for (EmployeeInsurance ins : records) {
            result.add(mapToInsuranceResponse(ins));
        }
        return result;
    }


    // QUERY 3b
    // auto-calculates the current Indian financial year
    // Financial year = April 1 of this year to March 31 of next year
    @Override
    public List<EmployeeInsuranceResponseDTO> getInsurancesForCurrentFinancialYear() {

        LocalDate today = LocalDate.now();

        LocalDate startDate = LocalDate.of(today.getYear(), 4, 1);

        // endDate is March 31 of next year
        LocalDate endDate = LocalDate.of(today.getYear() + 1, 3, 31);

        return getInsurancesAssignedBetween(startDate, endDate);
    }


    // QUERY 4
    // All pending claims — useful for admin dashboard

 // QUERY 4
 @Override
 public List<ClaimResponseDTO> getPendingClaims(int page, int size) {

     List<InsuranceClaim> allRecords = insuranceClaimRepository
             .findAllPendingClaims(
                 ClaimStatus.PENDING
             );

     // calculate start index
     int fromIndex = page * size;

     // if page is out of range return empty list
     if (fromIndex >= allRecords.size()) {
         return new ArrayList<>();
     }

     // toIndex should not go beyond list size
     int toIndex = fromIndex + size;
     if (toIndex > allRecords.size()) {
         toIndex = allRecords.size();
     }

     // cut the list to just this page
     List<InsuranceClaim> pageRecords = allRecords.subList(fromIndex, toIndex);

     // map to DTO
     List<ClaimResponseDTO> result = new ArrayList<>();
     for (InsuranceClaim claim : pageRecords) {
         result.add(mapToClaimResponse(claim));
     }
     return result;
 }


    // QUERY 5
    // Insurances expiring within the next N days
    // caller passes how many days
    @Override
    public List<EmployeeInsuranceResponseDTO> getInsurancesExpiringSoon(int days) {

        LocalDate today = LocalDate.now();
        LocalDate alertDate = today.plusDays(days); // today + 30

        List<EmployeeInsurance> records = employeeInsuranceRepository
                .findInsurancesExpiringBetween(
                    InsuranceStatus.ACTIVE,
                    today,
                    alertDate
                );

        List<EmployeeInsuranceResponseDTO> result = new ArrayList<>();
        for (EmployeeInsurance ins : records) {
            result.add(mapToInsuranceResponse(ins));
        }
        return result;
    }


    // MAPPERS
    private EmployeeInsuranceResponseDTO mapToInsuranceResponse(EmployeeInsurance ins) {
        EmployeeInsuranceResponseDTO dto = new EmployeeInsuranceResponseDTO();
        dto.setEmployeInsuranceId(ins.getId());
        dto.setEmployeeId(ins.getEmployee().getUserId());

        // fallback to username if firstName/lastName are blank (same fix as other services)
        String firstName = ins.getEmployee().getFirstName();
        String lastName  = ins.getEmployee().getLastName();
        String fullName;
        if (firstName != null && !firstName.isBlank() && lastName != null && !lastName.isBlank()) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = ins.getEmployee().getUsername();
        }
        dto.setEmployeeName(fullName);

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
        String fullName;
        if (firstName != null && !firstName.isBlank() && lastName != null && !lastName.isBlank()) {
            fullName = firstName + " " + lastName;
        } else {
            fullName = claim.getEmployee().getUsername();
        }
        dto.setEmployeeName(fullName);

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