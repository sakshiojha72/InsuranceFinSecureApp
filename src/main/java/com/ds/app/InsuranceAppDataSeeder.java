package com.ds.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ds.app.entity.*;
import com.ds.app.repository.*;
import com.ds.app.service.AppUserService;

@Configuration
public class InsuranceAppDataSeeder {

    private final Random random = new Random();

    @Bean
    CommandLineRunner seedAll(
            AppUserService appUserService,
            EmployeeRepository employeeRepo,
            InsurancePlanRepository planRepo,
            EmployeeInsuranceRepository empInsRepo,
            TopUpPlanRepository topUpRepo,
            EmployeeTopUpRepository empTopUpRepo,
            InsuranceClaimRepository claimRepo
    ) {
        return args -> {
            System.out.println(">>> Generating Comprehensive Insurance Dataset <<<");

            // 1. SYSTEM ADMINS (These will be the 'assignedBy' or 'resolvedBy' values)
            createUser(appUserService, "admin1", "admin123", UserRole.ADMIN);
            createUser(appUserService, "hr_manager", "hr123", UserRole.HR);
            createUser(appUserService, "finance_lead", "fin123", UserRole.FINANCE);

            // 2. DIVERSE INSURANCE PLANS
            InsurancePlan silver = createPlan(planRepo, "Basic Health Plan", 500000.0, "Essential surgery coverage", "admin1");
            InsurancePlan gold = createPlan(planRepo, "Standard Gold", 800000.0, "Standard family cover", "admin1");
            InsurancePlan platinum = createPlan(planRepo, "Platinum Elite", 1500000.0, "Global medical cover", "admin1");
            
            // For Scenario 2.8: Deactivated Plan
            InsurancePlan legacy = createPlan(planRepo, "Legacy 2023 Plan", 200000.0, "Discontinued", "admin1");
            legacy.setIsActive(false); planRepo.save(legacy);

            // 3. TOP-UP PLANS
            TopUpPlan dental = createTopUp(topUpRepo, "Dental Cover", 100000.0, 2000.0, "admin1");
            TopUpPlan accident = createTopUp(topUpRepo, "Accidental Death", 500000.0, 1500.0, "admin1");
            TopUpPlan covid = createTopUp(topUpRepo, "Covid Shield", 300000.0, 1000.0, "admin1");

            // 4. GENERATING EMPLOYEES FOR ALL POSTMAN SCENARIOS
            
            // Scenario 6.1: Employees WITH Top-Up (Bulk)
            for (int i = 1; i <= 5; i++) {
                Employee e = createEmployee(appUserService, employeeRepo, "emp_topup_" + i, "pass123", "Employee", "WithTopUp" + i);
                EmployeeInsurance ei = assignInsurance(empInsRepo, e, gold, LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(10), "hr_manager");
                buyTopUp(empTopUpRepo, e, (i % 2 == 0) ? dental : accident);
            }

            // Scenario 6.2: Employees WITHOUT Top-Up (Bulk)
            for (int i = 1; i <= 5; i++) {
                Employee e = createEmployee(appUserService, employeeRepo, "emp_none_" + i, "pass123", "Employee", "NoTopUp" + i);
                assignInsurance(empInsRepo, e, silver, LocalDate.now().minusDays(30), LocalDate.now().plusDays(335), "hr_manager");
            }

            // Scenario 6.6 & 6.7: Expiring Soon (Default 30 days & Custom days)
            Employee eExp1 = createEmployee(appUserService, employeeRepo, "emp_exp_10", "pass123", "Expiring", "Soon10");
            assignInsurance(empInsRepo, eExp1, silver, LocalDate.now().minusMonths(11), LocalDate.now().plusDays(10), "hr_manager");

            Employee eExp2 = createEmployee(appUserService, employeeRepo, "emp_exp_25", "pass123", "Expiring", "Soon25");
            assignInsurance(empInsRepo, eExp2, gold, LocalDate.now().minusMonths(11), LocalDate.now().plusDays(25), "hr_manager");

            // Scenario 6.4: Current Financial Year (Assigned recently)
            Employee eFy = createEmployee(appUserService, employeeRepo, "emp_fy_recent", "pass123", "Recent", "Joiner");
            assignInsurance(empInsRepo, eFy, platinum, LocalDate.now().minusDays(5), LocalDate.now().plusYears(1), "hr_manager");

            // Scenario 4.1 & 4.2: Claims Handling (Pending vs Approved)
            Employee eClaim = createEmployee(appUserService, employeeRepo, "emp_claimant", "pass123", "Claimant", "User");
            EmployeeInsurance eiClaim = assignInsurance(empInsRepo, eClaim, platinum, LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(9), "hr_manager");
            
            // Active Pending Claim (Blocks Scenario 4.2)
            createClaim(claimRepo, eClaim, eiClaim, 50000.0, "Emergency Surgery", ClaimStatus.PENDING, null);
            
            // History of Approved Claims
            createClaim(claimRepo, eClaim, eiClaim, 5000.0, "Consultation", ClaimStatus.APPROVED, "admin1");

            // Scenario 5.3: Employee with NO Insurance at all
            createEmployee(appUserService, employeeRepo, "emp_new_uninsured", "pass123", "New", "Hire");

            System.out.println(">>> Data Seeding Complete. All audit fields (assignedBy, resolvedBy) are populated. <<<");
        };
    }

    // --- HELPER METHODS TO ENSURE NO EMPTY COLUMNS ---

    private void createUser(AppUserService service, String user, String pass, UserRole role) {
        try {
            AppUser u = new AppUser();
            u.setUsername(user); u.setPassword(pass); u.setRole(role);
            service.registerAppUser(u);
        } catch (Exception e) {}
    }

    private Employee createEmployee(AppUserService service, EmployeeRepository repo, String user, String pass, String f, String l) {
        try {
            Employee e = new Employee();
            e.setUsername(user); e.setPassword(pass); e.setRole(UserRole.EMPLOYEE);
            e.setFirstName(f); e.setLastName(l);
            return (Employee) service.registerAppUser(e);
        } catch (Exception ex) {
            return (Employee) repo.findByUsername(user).get();
        }
    }

    private InsurancePlan createPlan(InsurancePlanRepository repo, String name, Double amt, String desc, String admin) {
        return repo.findByPlanName(name).orElseGet(() -> {
            InsurancePlan p = new InsurancePlan();
            p.setPlanName(name); p.setCoverageAmount(amt); p.setDescription(desc);
            p.setIsActive(true);
            p.setCreatedBy(admin); // Fills audit field
            return repo.save(p);
        });
    }

    private EmployeeInsurance assignInsurance(EmployeeInsuranceRepository repo, Employee e, InsurancePlan p, LocalDate start, LocalDate end, String admin) {
        return repo.findByEmployee_UserIdAndStatus(e.getUserId(), InsuranceStatus.ACTIVE).orElseGet(() -> {
            EmployeeInsurance ei = new EmployeeInsurance();
            ei.setEmployee(e);
            ei.setInsurancePlan(p);
            ei.setAssignedDate(start);
            ei.setExpiryDate(end);
            ei.setStatus(InsuranceStatus.ACTIVE);
            ei.setAssignedBy(admin); // Fills assignedBy logic
            return repo.save(ei);
        });
    }

    private TopUpPlan createTopUp(TopUpPlanRepository repo, String name, Double cover, Double price, String admin) {
        return repo.findByTopUpName(name).orElseGet(() -> {
            TopUpPlan t = new TopUpPlan();
            t.setTopUpName(name); t.setAdditionalCoverage(cover); t.setPrice(price);
            t.setIsActive(true);
            t.setCreatedBy(admin); // Fills audit field
            return repo.save(t);
        });
    }

    private void buyTopUp(EmployeeTopUpRepository repo, Employee e, TopUpPlan p) {
        EmployeeTopUp et = new EmployeeTopUp();
        et.setEmployee(e);
        et.setTopUpPlan(p);
        et.setPurchasedDate(LocalDate.now());
        et.setExpiryDate(LocalDate.now().plusYears(1));
        et.setStatus(InsuranceStatus.ACTIVE);
        repo.save(et);
    }

    private void createClaim(InsuranceClaimRepository repo, Employee e, EmployeeInsurance i, Double amt, String reason, ClaimStatus status, String resolver) {
        InsuranceClaim c = new InsuranceClaim();
        c.setEmployee(e);
        c.setEmployeeInsurance(i);
        c.setClaimAmount(amt);
        c.setReason(reason);
        c.setStatus(status);
        c.setRaisedAt(LocalDateTime.now().minusDays(10));
        
        if (status == ClaimStatus.APPROVED) {
            c.setResolvedAt(LocalDateTime.now().minusDays(5));
            c.setResolvedBy(resolver); // Matches Postman 4.7 logic
            c.setAdminRemarks("Claim verified and documents validated.");
        }
        repo.save(c);
    }
}