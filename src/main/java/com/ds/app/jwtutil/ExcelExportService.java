package com.ds.app.jwtutil;

import com.ds.app.dto.response.ClaimResponseDTO;
import com.ds.app.dto.response.EmployeeInsuranceResponseDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    // Workbook parameter isliye chahiye kyunki style workbook se tied hoti hai
    private CellStyle makeHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex()); // white text
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    // ── String array se ek row fill karne ka helper ──────────────────────────
    private void fillRow(Row row, String[] values) {
        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i] != null ? values[i] : "");
        }
    }

    // ── EmployeeInsuranceResponseDTO list → Excel bytes ──────────────────────
    public byte[] exportInsuranceList(
            List<EmployeeInsuranceResponseDTO> data, String sheetTitle) throws IOException {

        Workbook wb = new XSSFWorkbook(); // XSSFWorkbook = .xlsx format
        Sheet sheet = wb.createSheet(sheetTitle);

        String[] headers = {
            "Insurance ID", "Employee ID", "Employee Name",
            "Plan Name", "Coverage Amount (₹)",
            "Assigned Date", "Expiry Date", "Status"
        };
        Row headerRow = sheet.createRow(0); // row 0 = pehli row
        CellStyle headerStyle = makeHeaderStyle(wb);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle); 
        }

        int rowNum = 1;
        for (EmployeeInsuranceResponseDTO d : data) {
            Row row = sheet.createRow(rowNum++);
            fillRow(row, new String[]{
                String.valueOf(d.getEmployeeInsuranceId()), // note: typo in DTO field name
                String.valueOf(d.getEmployeeId()),
                d.getEmployeeName(),
                d.getPlanName(),
                String.valueOf(d.getCoverageAmount()),
                d.getAssignedDate() != null ? d.getAssignedDate().toString() : "",
                d.getExpiryDate()   != null ? d.getExpiryDate().toString()   : "",
                d.getStatus()       != null ? d.getStatus().toString()        : ""
            });
        }

        // 4. Columns auto-size karo (content ke hisaab se width)
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

        // 5. Workbook → bytes
        // ByteArrayOutputStream = memory ka ek "bucket" jisme bytes jaate hain
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);  
        wb.close();    
        return out.toByteArray(); // bytes return karo
    }

    // ── ClaimResponseDTO list → Excel bytes ──────────────────────────────────
    public byte[] exportClaimsList(
            List<ClaimResponseDTO> data, String sheetTitle) throws IOException {

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet(sheetTitle);

        String[] headers = {
            "Claim ID", "Employee ID", "Employee Name", "Plan Name",
            "Claim Amount (₹)", "Reason", "Status",
            "Raised At", "Resolved By", "Admin Remarks"
        };
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = makeHeaderStyle(wb);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (ClaimResponseDTO d : data) {
            Row row = sheet.createRow(rowNum++);
            fillRow(row, new String[]{
                String.valueOf(d.getClaimId()),
                String.valueOf(d.getEmployeeId()),
                d.getEmployeeName(),
                d.getPlanName(),
                String.valueOf(d.getClaimAmount()),
                d.getReason(),
                d.getStatus()     != null ? d.getStatus().toString()     : "",
                d.getRaisedAt()   != null ? d.getRaisedAt().toString()   : "",
                d.getResolvedBy()   != null ? d.getResolvedBy()          : "",
                d.getAdminRemarks() != null ? d.getAdminRemarks()        : ""
            });
        }

        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        return out.toByteArray();
    }
}