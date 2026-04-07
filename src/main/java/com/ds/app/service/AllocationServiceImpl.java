package com.ds.app.service;

import com.ds.app.client.AssetsServiceClient;
import com.ds.app.client.TimesheetServiceClient;
import com.ds.app.client.TrainingServiceClient;
import com.ds.app.dto.request.AllocationRequestDTO;
import com.ds.app.dto.request.DeallocationRequestDTO;
import com.ds.app.entity.*;
import com.ds.app.enums.AllocationAction;
import com.ds.app.exception.HrBusinessRuleException;
import com.ds.app.exception.HrException;
import com.ds.app.repository.iAllocationHistoryRepository;
import com.ds.app.repository.iEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AllocationServiceImpl {

    @Autowired private iEmployeeRepository employeeRepo;
    @Autowired private EmployeeServiceImpl employeeService;
    @Autowired private CompanyServiceImpl companyService;
    @Autowired private DepartmentServiceImpl departmentService;
    @Autowired private ProjectServiceImpl projectService;
    @Autowired private iAllocationHistoryRepository historyRepo;
    @Autowired private EmailServiceImpl emailService;
  
    // cross-module check stubs — replace internals when teams are ready
    @Autowired private TrainingServiceClient trainingClient;
    @Autowired private TimesheetServiceClient timesheetClient;
    @Autowired private AssetsServiceClient assetsClient;

    @Transactional
    public String assign(AllocationRequestDTO req,Long performedByUserId) {
    	
    	Employee performer = employeeService.findOrThrow(performedByUserId);
        Employee emp    = employeeService.findOrThrow(req.getEmployeeUserId());
        Company company = companyService.findOrThrow(req.getCompanyId());
        Department dept = departmentService.findOrThrow(req.getDepartmentId());
        
        
        
        //CHECK
        /*
         * Employee cannot be allocated to an InActive Entity*/
        if ("INACTIVE".equals(company.getStatus()))
            throw new HrBusinessRuleException ("Cannot assign employee to an inactive company: " + company.getName());

        if ("INACTIVE".equals(dept.getStatus()))
            throw new HrBusinessRuleException("Cannot assign employee to an inactive department: " + dept.getName());

        if (req.getProjectId() != null) {
            Project project = projectService.findOrThrow(req.getProjectId());
            if ("INACTIVE".equals(project.getStatus()) || "COMPLETED".equals(project.getStatus()))
                throw new HrBusinessRuleException("Cannot assign employee to a " + project.getStatus().toLowerCase() + " project: " + project.getName());
        }

        
        
        

       
        //  cross-module checks
        // Training check — is employee certified / training done?
        
        if (!trainingClient.isEmployeeCertified(emp.getUserId()))
            throw new HrBusinessRuleException("Employee has not completed training. Cannot assign to project.");

        
        // Timesheet check — does employee have an active timesheet?
        
        if (!timesheetClient.hasActiveTimesheet(emp.getUserId()))
            throw new HrBusinessRuleException("Employee has no active timesheet. Cannot assign.");
        

        // Assets check — does employee have open asset issues?
        if (!assetsClient.hasNoOpenIssues(emp.getUserId()))
            throw new HrBusinessRuleException("Employee has open asset issues. Resolve before assigning.");
        
        

        Project project = null;
        if (req.getProjectId() != null) {
            project = projectService.findOrThrow(req.getProjectId());
           
        }

        // Step 1 — update live state on employee
        emp.setCompany(company);
        emp.setDepartment(dept);
        emp.setProject(project);
        employeeRepo.save(emp);

        // Step 2 — write permanent history row
        saveHistory(
            emp.getUserId(),
            emp.getFirstName(),
            company.getId(), dept.getId(),
            project != null ? project.getId() : null,
            AllocationAction.ASSIGNED,
            performedByUserId.longValue(),
            performer.getFirstName()
            
        );
        
     // Step 3 — email
        emailService.sendAllocationEmail(
            emp.getEmail(), emp.getFirstName(),
            company.getName(), dept.getName(),
            project != null ? project.getName() : null
        );


//     

        return "Employee assigned successfully";
    }

    @Transactional
    public String deallocate(DeallocationRequestDTO req, Long performedByUserId) {

        Employee emp = employeeService.findOrThrow(req.getEmployeeUserId());
        Employee performer = employeeService.findOrThrow(performedByUserId);

        // snapshot BEFORE nulling — history captures where they were
        saveHistory(
            emp.getUserId().longValue(),
            emp.getFirstName(),
            emp.getCompanyId(), 
            emp.getDepartmentId(),
            emp.getProjectId(),
            AllocationAction.DEALLOCATED,
            performedByUserId,
            performer.getFirstName()
        );

        switch (req.getType().toUpperCase()) {
            case "PROJECT"    -> emp.setProject(null);
            case "DEPARTMENT" -> { emp.setDepartment(null); emp.setProject(null); }
            case "FULL"       -> { emp.setCompany(null); emp.setDepartment(null); emp.setProject(null); }
            default           -> throw new HrException("Invalid type. Use PROJECT / DEPARTMENT / FULL");
        }

        employeeRepo.save(emp);
        
        emailService.sendDeallocationEmail(emp.getEmail(), emp.getFirstName(), req.getType());
       
        return "Employee deallocated (" + req.getType() + ") successfully";
    }

    public List<AllocationHistory> getHistory(Long l) {
        return historyRepo.findByEmployeeIdOrderByActionAtAsc(l);
    }

    private void saveHistory(Long empId, String employeeName,Long companyId, Long deptId,
                              Long projectId, AllocationAction action, Long performedById,String performerName) {
        AllocationHistory h = new AllocationHistory();
        h.setEmployeeId(empId);
        h.setEmployeeName(employeeName);
        h.setCompanyId(companyId);
        h.setDepartmentId(deptId);
        h.setProjectId(projectId);
        h.setAction(action);
        h.setActionAt(LocalDateTime.now());
        h.setPerformedById(performedById);
        h.setPerformerName(performerName);
        historyRepo.save(h);
    }
}
