package com.ds.app.service;

import com.ds.app.dto.response.CompanyDetailDTO;
import com.ds.app.entity.Company;
import com.ds.app.entity.Department;
import com.ds.app.enums.EscalationStatus;
import com.ds.app.repository.iCompanyRepository;
import com.ds.app.repository.iDepartmentRepository;
import com.ds.app.repository.iEmployeeRepository;
import com.ds.app.repository.iEscalationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl {

    @Autowired private iEmployeeRepository employeeRepo;
    @Autowired private iEscalationRepository escalationRepo;
    @Autowired private iCompanyRepository companyRepo;
    @Autowired private iDepartmentRepository deptRepo;
    @Autowired private EmployeeServiceImpl employeeService;

    // Report 1: Summary dashboard 
    // Returns key counts across the whole system
    public Map<String, Long> getSummary() {
        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("totalEmployees",        employeeRepo.countByIsDeletedFalse());
        summary.put("escalatedEmployees",    employeeRepo.countByIsEscalatedTrue());
        summary.put("openEscalations",       escalationRepo.countByStatus(EscalationStatus.OPEN));
        summary.put("inProgressEscalations", escalationRepo.countByStatus(EscalationStatus.IN_PROGRESS));
        summary.put("resolvedEscalations",   escalationRepo.countByStatus(EscalationStatus.RESOLVED));
        return summary;
    }

    // Report 2: Count employees per company
    // Returns { "ICICI Bank": 12, "Citi": 8 }
    public Map<String, Long> countGroupByCompany() {
        List<Object[]> rows = employeeRepo.countGroupByCompany();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Long companyId = (Long) row[0];
            Long count     = (Long) row[1];
            // resolve company name for readable output
            String name = companyRepo.findById(companyId)
                    .map(Company::getName)
                    .orElse("Company-" + companyId);
            result.put(name, count);
        }
        return result;
    }

    // Report 3: Count employees per department 
    // Returns { "Risk & Compliance": 5, "Tech": 10 }
    public Map<String, Long> countGroupByDepartment() {
        List<Object[]> rows = employeeRepo.countGroupByDepartment();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Long deptId = (Long) row[0];
            Long count  = (Long) row[1];
            String name = deptRepo.findById(deptId)
                    .map(Department::getName)
                    .orElse("Dept-" + deptId);
            result.put(name, count);
        }
        return result;
    }

    //Report 4: Count employees per type 
    // Returns { "FRESHER": 20, "EXPERIENCED": 15, "CERTIFIED": 8 }
    public Map<String, Long> countGroupByEmployeeType() {
        List<Object[]> rows = employeeRepo.countGroupByEmployeeType();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String type = row[0] != null ? row[0].toString() : "NOT_SET";
            result.put(type, (Long) row[1]);
        }
        return result;
    }

    //  Report 5: Count employees per status 
    // Returns { "ACTIVE": 40, "INACTIVE": 3, "TERMINATED": 1 }
    public Map<String, Long> countGroupByStatus() {
        List<Object[]> rows = employeeRepo.countGroupByStatus();
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put(row[0].toString(), (Long) row[1]);
        }
        return result;
    }

    //Report 6: Count in a specific company
    public long countByCompany(Long companyId) {
        return employeeService.countByCompany(companyId);
    }

    // Report 7: Count in a specific department 
    public long countByDepartment(Long deptId) {
        return employeeService.countByDepartment(deptId);
    }
    
    
    //Report 8: Company perspective business Report
    public Map<String, Map<String, List<String>>> getCompanyPerspectiveReport() {
        List<CompanyDetailDTO> data = employeeRepo.getDetailedCompanyReport();

        return data.stream().collect(Collectors.groupingBy(
                CompanyDetailDTO::getCompanyName,
                Collectors.groupingBy(
                        CompanyDetailDTO::getDepartmentName,
                        Collectors.mapping(
                            dto -> dto.getEmployeeName() + " (Project: " + 
                                   (dto.getProjectName() != null ? dto.getProjectName() : "Bench") + ")",
                            Collectors.toList()
                        )
                )
        ));
    }


}
