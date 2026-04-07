package com.ds.app.service;

import com.ds.app.client.*;
import com.ds.app.dto.request.AllocationRequestDTO;
import com.ds.app.dto.request.DeallocationRequestDTO;
import com.ds.app.entity.*;
import com.ds.app.enums.AllocationAction;
import com.ds.app.exception.HrException;
import com.ds.app.repository.iAllocationHistoryRepository;
import com.ds.app.repository.iEmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AllocationServiceImpl Tests")
class AllocationServiceImplTest {

    @InjectMocks private AllocationServiceImpl allocationService;

    @Mock private iEmployeeRepository employeeRepo;
    @Mock private EmployeeServiceImpl employeeService;
    @Mock private CompanyServiceImpl companyService;
    @Mock private DepartmentServiceImpl departmentService;
    @Mock private ProjectServiceImpl projectService;
    @Mock private iAllocationHistoryRepository historyRepo;
    @Mock private EmailServiceImpl emailService;
    @Mock private TrainingServiceClient trainingClient;
    @Mock private TimesheetServiceClient timesheetClient;
    @Mock private AssetsServiceClient assetsClient;

    private Employee emp, performer;
    private Company company;
    private Department dept;
    private Project project;

    @BeforeEach
    void setUp() {
        company = new Company(); company.setId(1L); company.setName("ICICI Bank");
        dept    = new Department(); dept.setId(1L); dept.setName("Risk"); dept.setCompany(company);
        project = new Project(); project.setId(1L); project.setName("Alpha");

        emp = new Employee();
        emp.setUserId(10L);
        emp.setUsername("john@finsecure.com");
        emp.setFirstName("John");
        emp.setEmail("john@finsecure.com");
        emp.setIsDeleted(false);
        emp.setCompany(company);
        emp.setDepartment(dept);
        emp.setProject(project);

        performer = new Employee();
        performer.setUserId(1L);
        performer.setFirstName("Riya");  // HR user
        performer.setEmail("hr@finsecure.com");
    }

    // ── assign: success without project ──────────────────────────────
    @Test
    @DisplayName("assign: successfully assigns employee to company and department")
    void assign_success_noProject() {
        AllocationRequestDTO req = new AllocationRequestDTO();
        req.setEmployeeUserId(10L); req.setCompanyId(1L);
        req.setDepartmentId(1L);   req.setProjectId(null);

        when(employeeService.findOrThrow(10L)).thenReturn(emp);
        when(employeeService.findOrThrow(1L)).thenReturn(performer);
        when(companyService.findOrThrow(1L)).thenReturn(company);
        when(departmentService.findOrThrow(1L)).thenReturn(dept);
        when(trainingClient.isEmployeeCertified(any())).thenReturn(true);
        when(timesheetClient.hasActiveTimesheet(any())).thenReturn(true);
        when(assetsClient.hasNoOpenIssues(any())).thenReturn(true);
        when(employeeRepo.save(any())).thenReturn(emp);
        when(historyRepo.save(any())).thenReturn(new AllocationHistory());

        String result = allocationService.assign(req, 1L);

        assertEquals("Employee assigned successfully", result);
        verify(employeeRepo).save(emp);
        verify(historyRepo).save(any(AllocationHistory.class));
        verify(emailService).sendAllocationEmail(
                eq("john@finsecure.com"), eq("John"),
                eq("ICICI Bank"), eq("Risk"), isNull());
    }

    // ── assign: success with project ─────────────────────────────────
    @Test
    @DisplayName("assign: successfully assigns employee including project")
    void assign_success_withProject() {
        AllocationRequestDTO req = new AllocationRequestDTO();
        req.setEmployeeUserId(10L); req.setCompanyId(1L);
        req.setDepartmentId(1L);   req.setProjectId(1L);

        when(employeeService.findOrThrow(10L)).thenReturn(emp);
        when(employeeService.findOrThrow(1L)).thenReturn(performer);
        when(companyService.findOrThrow(1L)).thenReturn(company);
        when(departmentService.findOrThrow(1L)).thenReturn(dept);
        when(projectService.findOrThrow(1L)).thenReturn(project);
        when(trainingClient.isEmployeeCertified(any())).thenReturn(true);
        when(timesheetClient.hasActiveTimesheet(any())).thenReturn(true);
        when(assetsClient.hasNoOpenIssues(any())).thenReturn(true);
        when(employeeRepo.save(any())).thenReturn(emp);
        when(historyRepo.save(any())).thenReturn(new AllocationHistory());

        String result = allocationService.assign(req, 1L);

        assertEquals("Employee assigned successfully", result);
        assertEquals(company, emp.getCompany());
        assertEquals(dept,    emp.getDepartment());
        assertEquals(project, emp.getProject());
    }

    // ── assign: training check fails ─────────────────────────────────
    @Test
    @DisplayName("assign: throws HrException when training not complete")
    void assign_fails_trainingNotComplete() {
        AllocationRequestDTO req = new AllocationRequestDTO();
        req.setEmployeeUserId(10L); req.setCompanyId(1L); req.setDepartmentId(1L);

        when(employeeService.findOrThrow(10L)).thenReturn(emp);
        when(employeeService.findOrThrow(1L)).thenReturn(performer);
        when(companyService.findOrThrow(1L)).thenReturn(company);
        when(departmentService.findOrThrow(1L)).thenReturn(dept);
        when(trainingClient.isEmployeeCertified(any())).thenReturn(false);

        HrException ex = assertThrows(HrException.class,
                () -> allocationService.assign(req, 1L));
        assertTrue(ex.getMessage().contains("training"));
        verify(employeeRepo, never()).save(any());
    }

    // ── assign: timesheet check fails ────────────────────────────────
    @Test
    @DisplayName("assign: throws HrException when no active timesheet")
    void assign_fails_noActiveTimesheet() {
        AllocationRequestDTO req = new AllocationRequestDTO();
        req.setEmployeeUserId(10L); req.setCompanyId(1L); req.setDepartmentId(1L);

        when(employeeService.findOrThrow(10L)).thenReturn(emp);
        when(employeeService.findOrThrow(1L)).thenReturn(performer);
        when(companyService.findOrThrow(1L)).thenReturn(company);
        when(departmentService.findOrThrow(1L)).thenReturn(dept);
        when(trainingClient.isEmployeeCertified(any())).thenReturn(true);
        when(timesheetClient.hasActiveTimesheet(any())).thenReturn(false);

        HrException ex = assertThrows(HrException.class,
                () -> allocationService.assign(req, 1L));
        assertTrue(ex.getMessage().contains("timesheet"));
        verify(employeeRepo, never()).save(any());
    }

    // ── assign: assets check fails ────────────────────────────────────
    @Test
    @DisplayName("assign: throws HrException when open asset issues")
    void assign_fails_openAssetIssues() {
        AllocationRequestDTO req = new AllocationRequestDTO();
        req.setEmployeeUserId(10L); req.setCompanyId(1L); req.setDepartmentId(1L);

        when(employeeService.findOrThrow(10L)).thenReturn(emp);
        when(employeeService.findOrThrow(1L)).thenReturn(performer);
        when(companyService.findOrThrow(1L)).thenReturn(company);
        when(departmentService.findOrThrow(1L)).thenReturn(dept);
        when(trainingClient.isEmployeeCertified(any())).thenReturn(true);
        when(timesheetClient.hasActiveTimesheet(any())).thenReturn(true);
        when(assetsClient.hasNoOpenIssues(any())).thenReturn(false);

        HrException ex = assertThrows(HrException.class,
                () -> allocationService.assign(req, 1L));
        assertTrue(ex.getMessage().contains("asset"));
        verify(employeeRepo, never()).save(any());
    }

    // ── assign: AllocationHistory captures names ──────────────────────
    @Test
    @DisplayName("assign: saves history row with employee and performer names")
    void assign_savesHistoryWithNames() {
        AllocationRequestDTO req = new AllocationRequestDTO();
        req.setEmployeeUserId(10L); req.setCompanyId(1L); req.setDepartmentId(1L);

        when(employeeService.findOrThrow(10L)).thenReturn(emp);
        when(employeeService.findOrThrow(1L)).thenReturn(performer);
        when(companyService.findOrThrow(1L)).thenReturn(company);
        when(departmentService.findOrThrow(1L)).thenReturn(dept);
        when(trainingClient.isEmployeeCertified(any())).thenReturn(true);
        when(timesheetClient.hasActiveTimesheet(any())).thenReturn(true);
        when(assetsClient.hasNoOpenIssues(any())).thenReturn(true);
        when(employeeRepo.save(any())).thenReturn(emp);

        allocationService.assign(req, 1L);

        // verify history was saved — names are now part of the row
        verify(historyRepo).save(argThat(h ->
                "John".equals(h.getEmployeeName()) &&
                "Riya".equals(h.getPerformerName()) &&
                h.getAction() == AllocationAction.ASSIGNED
        ));
    }

    // ── deallocate: PROJECT nulls only project ────────────────────────
    @Test
    @DisplayName("deallocate: PROJECT type — only project is nulled")
    void deallocate_project_nuллsOnlyProject() {
        DeallocationRequestDTO req = new DeallocationRequestDTO();
        req.setEmployeeUserId(10L); req.setType("PROJECT");

        when(employeeService.findOrThrow(10L)).thenReturn(emp);
        when(employeeService.findOrThrow(1L)).thenReturn(performer);
        when(historyRepo.save(any())).thenReturn(new AllocationHistory());
        when(employeeRepo.save(any())).thenReturn(emp);

        allocationService.deallocate(req, 1L);

        assertNull(emp.getProject());
        assertNotNull(emp.getDepartment());
        assertNotNull(emp.getCompany());
    }

    // ── deallocate: DEPARTMENT nulls dept + project ───────────────────
    @Test
    @DisplayName("deallocate: DEPARTMENT type — dept and project nulled")
    void deallocate_department_nullsDeptAndProject() {
        DeallocationRequestDTO req = new DeallocationRequestDTO();
        req.setEmployeeUserId(10L); req.setType("DEPARTMENT");

        when(employeeService.findOrThrow(10L)).thenReturn(emp);
        when(employeeService.findOrThrow(1L)).thenReturn(performer);
        when(historyRepo.save(any())).thenReturn(new AllocationHistory());
        when(employeeRepo.save(any())).thenReturn(emp);

        allocationService.deallocate(req, 1L);

        assertNull(emp.getDepartment());
        assertNull(emp.getProject());
        assertNotNull(emp.getCompany());
    }

    // ── deallocate: FULL nulls everything ─────────────────────────────
    @Test
    @DisplayName("deallocate: FULL type — company, dept and project all nulled")
    void deallocate_full_nullsAll() {
        DeallocationRequestDTO req = new DeallocationRequestDTO();
        req.setEmployeeUserId(10L); req.setType("FULL");

        when(employeeService.findOrThrow(10L)).thenReturn(emp);
        when(employeeService.findOrThrow(1L)).thenReturn(performer);
        when(historyRepo.save(any())).thenReturn(new AllocationHistory());
        when(employeeRepo.save(any())).thenReturn(emp);

        allocationService.deallocate(req, 1L);

        assertNull(emp.getProject());
        assertNull(emp.getDepartment());
        assertNull(emp.getCompany());
    }

    // ── deallocate: invalid type ──────────────────────────────────────
    @Test
    @DisplayName("deallocate: invalid type string throws HrException")
    void deallocate_invalidType_throwsHrException() {
        DeallocationRequestDTO req = new DeallocationRequestDTO();
        req.setEmployeeUserId(10L); req.setType("INVALID");

        when(employeeService.findOrThrow(10L)).thenReturn(emp);
        when(employeeService.findOrThrow(1L)).thenReturn(performer);
        when(historyRepo.save(any())).thenReturn(new AllocationHistory());

        HrException ex = assertThrows(HrException.class,
                () -> allocationService.deallocate(req, 1L));
        assertTrue(ex.getMessage().contains("Invalid type"));
    }

    // ── getHistory ────────────────────────────────────────────────────
    @Test
    @DisplayName("getHistory: returns ordered history list for employee")
    void getHistory_returnsOrderedList() {
        AllocationHistory h1 = new AllocationHistory(); h1.setAction(AllocationAction.ASSIGNED);
        AllocationHistory h2 = new AllocationHistory(); h2.setAction(AllocationAction.DEALLOCATED);

        when(historyRepo.findByEmployeeIdOrderByActionAtAsc(10L))
                .thenReturn(List.of(h1, h2));

        List<AllocationHistory> result = allocationService.getHistory(10L);

        assertEquals(2, result.size());
        assertEquals(AllocationAction.ASSIGNED,    result.get(0).getAction());
        assertEquals(AllocationAction.DEALLOCATED, result.get(1).getAction());
    }
}
