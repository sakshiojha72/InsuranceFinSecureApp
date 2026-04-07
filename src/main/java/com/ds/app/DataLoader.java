package com.ds.app;

import com.ds.app.entity.*;


import com.ds.app.enums.AllocationAction;
import com.ds.app.enums.CertificationStatus;
import com.ds.app.enums.EmployeeExperience;
import com.ds.app.enums.EscalationStatus;
import com.ds.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Component
public class DataLoader implements CommandLineRunner {


    @Autowired private iAppUserRepository userRepo;
    @Autowired private iEmployeeRepository employeeRepo;
    @Autowired private iCompanyRepository companyRepo;
    @Autowired private iDepartmentRepository deptRepo;
    @Autowired private iProjectRepository projectRepo;
    @Autowired private iEscalationRepository escalationRepo;
    @Autowired private iAppraisalRepository appraisalRepo;
    @Autowired private iAllocationHistoryRepository historyRepo;
    @Autowired private PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {
        // skip seeding if data already present
        if (employeeRepo.count() > 0) {
            System.out.println("[Seeder] Data already exists — skipping seed");
            return;
        }


        System.out.println("[Seeder] Starting data seed...");


        //COMPANIES
        Company icici = company("ICICI Bank", "ICICI-001", true);
        Company citi  = company("Citi Bank",  "CITI-001",  true);
        companyRepo.saveAll(List.of(icici, citi));


        // DEPARTMENTS 
        Department iciciRisk   = dept("Risk Management", "RISK", icici);
        Department iciciTech   = dept("Technology",      "TECH", icici);
        Department iciciComp   = dept("Compliance",      "COMP", icici);
        Department citiRisk    = dept("Risk Management", "RISK", citi);
        Department citiFinance = dept("Finance Ops",     "FIN",  citi);
        deptRepo.saveAll(List.of(iciciRisk, iciciTech, iciciComp, citiRisk, citiFinance));


        // PROJECTS
        Project basel   = project("Basel III Compliance", icici, iciciRisk,
                                   LocalDate.of(2024, 1, 1), null);
        Project digital = project("Digital Banking Platform", icici, iciciTech,
                                   LocalDate.of(2024, 3, 1), LocalDate.of(2025, 3, 1));
        Project aml     = project("AML Monitoring System", citi, citiRisk,
                                   LocalDate.of(2024, 6, 1), null);
        projectRepo.saveAll(List.of(basel, digital, aml));


        // USERS & EMPLOYEES ------------------

        // Admin user — can view everything
        AppUser adminUser = appUser("Admin", "Admin123", "ADMIN");
        userRepo.save(adminUser);


        // HR users — full access
        Employee hr1 = employee("EMP-HR-01", "Bhawna",    "Sharma",  "Bhawna",
                "HR123", "HR", 75000.0, LocalDate.of(2022, 6, 1), null, null, null, true);
        Employee hr2 = employee("EMP-HR-02", "Abhishek",   "Mehta",   "Abhishek",
                "HR123", "HR", 72000.0, LocalDate.of(2022, 9, 1), null, null, null, true);
        employeeRepo.saveAll(List.of(hr1, hr2));


        // Manager users — can raise escalations within dept
        Employee mgr1 = employee("EMP-MG-01", "Arjun",  "Nair",    "Arjun",
                "Mgr123", "MANAGER", 90000.0, LocalDate.of(2021, 1, 15),
                icici, iciciRisk, basel, true);
        Employee mgr2 = employee("EMP-MG-02", "Preethi","Rao",     "Preethi",
                "Mgr123", "MANAGER", 88000.0, LocalDate.of(2021, 4, 1),
                icici, iciciTech, digital, true);
        employeeRepo.saveAll(List.of(mgr1, mgr2));


        // Regular employees — limited view access
        Employee emp1 = employee("EMP-001", "John",    "D'Souza",  "John",
                "Emp123", "EMPLOYEE", 55000.0, LocalDate.of(2023, 1, 10),
                icici, iciciRisk, basel, true);
        Employee emp2 = employee("EMP-002", "Priya",   "Pillai",   "Priya",
                "Emp123", "EMPLOYEE", 52000.0, LocalDate.of(2023, 3, 5),
                icici, iciciTech, digital, false);
        Employee emp3 = employee("EMP-003", "Rahul",   "Gupta",    "Rahul",
                "Emp123", "EMPLOYEE", 48000.0, LocalDate.of(2023, 6, 1),
                icici, iciciComp, null, false);
        Employee emp4 = employee("EMP-004", "Sara",    "Thomas",   "Sara",
                "Emp123", "EMPLOYEE", 61000.0, LocalDate.of(2022, 11, 1),
                citi, citiRisk, aml, true);
        Employee emp5 = employee("EMP-005", "Aditya",  "Joshi",    "Aditya",
                "Emp123", "EMPLOYEE", 45000.0, LocalDate.of(2024, 1, 15),
                citi, citiFinance, null, false);
        // unassigned employee — for testing unassigned filter
        Employee emp6 = employee("EMP-006", "Sneha",   "Kulkarni", "Sneha",
                "Emp123", "EMPLOYEE", 42000.0, LocalDate.of(2024, 2, 1),
                null, null, null, false);
        employeeRepo.saveAll(List.of(emp1, emp2, emp3, emp4, emp5, emp6));


        // ESCALATIONS 
        // raised by manager against emp2 — escalated flag set
        Escalation esc1 = new Escalation();
        esc1.setRaisedBy(mgr2);
        esc1.setTargetEmployee(emp2);
        esc1.setDepartmentId(iciciTech.getId());
        esc1.setCompanyId(icici.getId());
        esc1.setComment("Repeated deadline misses on Digital Banking Platform. Third occurrence this quarter.");
        esc1.setStatus(EscalationStatus.OPEN);
        esc1.setRaisedAt(LocalDateTime.now().minusDays(5));
        escalationRepo.save(esc1);


        emp2.setIsEscalated(true);
        employeeRepo.save(emp2);


        // resolved escalation — history
        Escalation esc2 = new Escalation();
        esc2.setRaisedBy(hr1);
        esc2.setTargetEmployee(emp3);
        esc2.setDepartmentId(iciciComp.getId());
        esc2.setCompanyId(icici.getId());
        esc2.setComment("Compliance training not completed within given timeframe.");
        esc2.setStatus(EscalationStatus.RESOLVED);
        esc2.setRaisedAt(LocalDateTime.now().minusDays(30));
        esc2.setResolvedAt(LocalDateTime.now().minusDays(20));
        escalationRepo.save(esc2);


        // APPRAISALS
        Appraisal ap1 = new Appraisal();
        ap1.setEmployee(emp1);
        ap1.setInitiatedByHr(hr1);
        ap1.setPreviousSalary(50000.0);
        ap1.setRevisedSalary(55000.0);
        ap1.setRemarks("Excellent performance on Basel III project. Promoted to senior consultant.");
        ap1.setAppraisalYear(2024);
        ap1.setAppraisalDate(LocalDate.of(2024, 4, 1));
        appraisalRepo.save(ap1);


        Appraisal ap2 = new Appraisal();
        ap2.setEmployee(emp4);
        ap2.setInitiatedByHr(hr2);
        ap2.setPreviousSalary(58000.0);
        ap2.setRevisedSalary(61000.0);
        ap2.setRemarks("Strong AML expertise demonstrated. Consistent delivery.");
        ap2.setAppraisalYear(2024);
        ap2.setAppraisalDate(LocalDate.of(2024, 5, 15));
        appraisalRepo.save(ap2);


        //ALLOCATION HISTORY
        history(emp1.getUserId(), icici.getId(), iciciRisk.getId(), basel.getId(),
                AllocationAction.ASSIGNED, hr1.getUserId(), LocalDateTime.now().minusDays(60));
        history(emp4.getUserId(), citi.getId(), citiRisk.getId(), aml.getId(),
                AllocationAction.ASSIGNED, hr2.getUserId(), LocalDateTime.now().minusDays(45));


      
       
        System.out.println("[Seeder] Login credentials:");
        System.out.println("[Seeder]   Admin   → admin finsecure.com  / Admin 123");
        System.out.println("[Seeder]   HR      → Bhawna    / HR123");
        System.out.println("[Seeder]   HR      → Abhishek   / HR123");
        System.out.println("[Seeder]   Manager → Arjun  / Mgr123");
        System.out.println("[Seeder]   Manager → Preethi   / Mgr123");
        System.out.println("[Seeder]   Employee→ john    / Emp123");
        System.out.println("[Seeder]   Employee→ sneha  / Emp123 (unassigned)");
    }


    //----------builder helpers----------------


    private Company company(String name, String code, boolean restricts) {
        Company c = new Company();
        c.setName(name); c.setCode(code);
        c.setRestrictsInvestment(restricts);
        c.setStatus("ACTIVE");
        return c;
    }


    private Department dept(String name, String code, Company company) {
        Department d = new Department();
        d.setName(name); d.setCode(code);
        d.setCompany(company); d.setStatus("ACTIVE");
        return d;
    }


    private Project project(String name, Company company, Department dept,
                             LocalDate start, LocalDate end) {
        Project p = new Project();
        p.setName(name); p.setCompany(company);
        p.setDepartment(dept); p.setStatus("ACTIVE");
        p.setStartDate(start); p.setEndDate(end);
        return p;
    }


    private AppUser appUser(String username, String rawPassword, String role) {
        AppUser u = new AppUser();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole(UserRole.valueOf(role));
        u.setIsAccountLocked(false);
        u.setFailedLoginAttemptsCount(0);
        return u;
    }


    private Employee employee(String code, String firstName, String lastName,
                               String username, String rawPassword, String role,
                               Double salary, LocalDate joiningDate,
                               Company company, Department dept,
                               Project project, Boolean certified) {
        Employee e = new Employee();
        e.setUsername(username);
        e.setPassword(passwordEncoder.encode(rawPassword));
        e.setRole(UserRole.valueOf(role));
        e.setIsAccountLocked(false);
        e.setEmail("bhavvvvvs@gmail.com");
        e.setFailedLoginAttemptsCount(0);
        e.setEmployeeCode(code);
        e.setFirstName(firstName);
        e.setLastName(lastName);
        e.setEmail(username);
        e.setCurrentSalary(salary);
        e.setJoiningDate(joiningDate);
        e.setCompany(company);
        e.setDepartment(dept);
        e.setProject(project);
      
        e.setIsEscalated(false);
       
        e.setIsDeleted(false);
        e.setEmployeeExperience( EmployeeExperience.FRESHER);
        return e;
    }


    private void history(Long empId, Long compId, Long deptId, Long projId,
                          AllocationAction action, Long performedById, LocalDateTime at) {
        AllocationHistory h = new AllocationHistory();
        h.setEmployeeId(empId); h.setCompanyId(compId);
        h.setDepartmentId(deptId); h.setProjectId(projId);
        h.setAction(action); h.setActionAt(at);
        h.setPerformedById(performedById);
        historyRepo.save(h);
    }
}













