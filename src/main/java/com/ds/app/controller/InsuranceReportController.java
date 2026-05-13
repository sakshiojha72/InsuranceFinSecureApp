package com.ds.app.controller;

import com.ds.app.dto.response.ClaimResponseDTO;
import com.ds.app.dto.response.EmployeeInsuranceResponseDTO;
import com.ds.app.jwtutil.ExcelExportService;
import com.ds.app.service.InsuranceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Insurance Reports", description = "Business reports for ADMIN and HR")
@RestController
@RequestMapping("/finsecure/insurance/reports")
public class InsuranceReportController {

    @Autowired
    private InsuranceReportService insuranceReportService;
    
    @Autowired
    private ExcelExportService excelExportService;

    // Query 1 — employees with insurance AND top-up
    @Operation(summary = "Employees with insurance AND top-up", description = "Returns employees who have both active base insurance and at least one active top-up.")
    @GetMapping("/with-topup")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    public ResponseEntity<List<EmployeeInsuranceResponseDTO>> getWithTopUp(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            insuranceReportService.getEmployeesWithInsuranceAndTopUp(page, size)
        );
    }

    // Query 2 — employees with insurance but NO top-up
    @Operation(summary = "Employees with NO top-up", description = "Returns employees who have active insurance but have not purchased any top-up.")
    @GetMapping("/no-topup")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    public ResponseEntity<List<EmployeeInsuranceResponseDTO>> getWithNoTopUp() {
        return ResponseEntity.ok(
            insuranceReportService.getEmployeesWithNoTopUp()
        );
    }

    // Query 3a — insurances assigned between two custom dates
    @Operation(summary = "Insurances assigned between dates", description = "Returns all insurances assigned within a custom date range.")
    @GetMapping("/assigned-between")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    public ResponseEntity<List<EmployeeInsuranceResponseDTO>> getAssignedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(
            insuranceReportService.getInsurancesAssignedBetween(startDate, endDate)
        );
    }

    // Query 3b — current Indian financial year
    @Operation(summary = "Insurances in current financial year", description = "Returns insurances assigned in the current Indian financial year (April to March).")
    @GetMapping("/current-financial-year")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    public ResponseEntity<List<EmployeeInsuranceResponseDTO>> getCurrentFinancialYear() {
        return ResponseEntity.ok(
            insuranceReportService.getInsurancesForCurrentFinancialYear()
        );
    }

    // Query 4 — pending claims
    @Operation(summary = "Pending claims report", description = "Returns all claims currently in PENDING status.")
    @GetMapping("/pending-claims")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    public ResponseEntity<List<ClaimResponseDTO>> getPendingClaims(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            insuranceReportService.getPendingClaims(page, size)
        );
    }

    // Query 5 — expiring soon
    @Operation(summary = "Expiring soon", description = "Returns insurances expiring within the given number of days. Default is 30 days.")
    @GetMapping("/expiring-soon")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    public ResponseEntity<List<EmployeeInsuranceResponseDTO>> getExpiringSoon(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(
            insuranceReportService.getInsurancesExpiringSoon(days)
        );
    }
    
 // ── Excel Download endpoints ─────────────────────────────────────────────────
 // Download: employees with top-up
    @GetMapping("/with-topup/download")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    public ResponseEntity<byte[]> downloadWithTopUp() throws Exception {
        // service se data lo (existing method reuse)
        List<EmployeeInsuranceResponseDTO> data =
            insuranceReportService.getEmployeesWithInsuranceAndTopUp(0, Integer.MAX_VALUE);
        byte[] excel = excelExportService.exportInsuranceList(data, "With TopUp");
        return excelResponse(excel, "report_with_topup.xlsx");
    }

    // Download: employees with NO top-up
    @GetMapping("/no-topup/download")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    public ResponseEntity<byte[]> downloadNoTopUp() throws Exception {
        List<EmployeeInsuranceResponseDTO> data =
            insuranceReportService.getEmployeesWithNoTopUp();
        byte[] excel = excelExportService.exportInsuranceList(data, "No TopUp");
        return excelResponse(excel, "report_no_topup.xlsx");
    }

    // Download: assigned between dates
    @GetMapping("/assigned-between/download")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    public ResponseEntity<byte[]> downloadAssignedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws Exception {
        List<EmployeeInsuranceResponseDTO> data =
            insuranceReportService.getInsurancesAssignedBetween(startDate, endDate);
        byte[] excel = excelExportService.exportInsuranceList(data, "Assigned Between");
        return excelResponse(excel, "report_assigned_between.xlsx");
    }

    // Download: pending claims
    @GetMapping("/pending-claims/download")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    public ResponseEntity<byte[]> downloadPendingClaims() throws Exception {
        List<ClaimResponseDTO> data =
            insuranceReportService.getPendingClaims(0, Integer.MAX_VALUE);
        byte[] excel = excelExportService.exportClaimsList(data, "Pending Claims");
        return excelResponse(excel, "report_pending_claims.xlsx");
    }

    // Download: expiring soon
    @GetMapping("/expiring-soon/download")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HR')")
    public ResponseEntity<byte[]> downloadExpiringSoon(
            @RequestParam(defaultValue = "30") int days) throws Exception {
        List<EmployeeInsuranceResponseDTO> data =
            insuranceReportService.getInsurancesExpiringSoon(days);
        byte[] excel = excelExportService.exportInsuranceList(data, "Expiring Soon");
        return excelResponse(excel, "report_expiring_soon.xlsx");
    }

    // ── Shared helper: ResponseEntity with Excel headers banana ─────────────────
    private ResponseEntity<byte[]> excelResponse(byte[] data, String filename) {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(data);
    }
    
}

