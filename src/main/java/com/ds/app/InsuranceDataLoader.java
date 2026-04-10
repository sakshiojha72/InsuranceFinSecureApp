package com.ds.app;

import com.ds.app.entity.*;
import com.ds.app.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class InsuranceDataLoader {

    private static final String ADMIN_USERNAME = "admin1";

    @Bean
    CommandLineRunner seedInsuranceData(
            iAppUserRepository    userRepo,
            EmployeeRepository    employeeRepo,
            InsurancePlanRepository      insurancePlanRepo,
            EmployeeInsuranceRepository  employeeInsuranceRepo,
            TopUpPlanRepository          topUpPlanRepo,
            EmployeeTopUpRepository      employeeTopUpRepo,
            InsuranceClaimRepository     insuranceClaimRepo,
            PasswordEncoder              passwordEncoder) {

        return args -> {

            // ── ADMIN (AppUser, not Employee) ────────────────────────────────
        	if (userRepo.findByUsername(ADMIN_USERNAME).isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername(ADMIN_USERNAME);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(UserRole.ADMIN);
                userRepo.save(admin);
                System.out.println("✅ Admin created — " + ADMIN_USERNAME);
            }

            // ── HR (AppUser, not Employee) ───────────────────────────────────
        	if (userRepo.findByUsername("hr_manager").isEmpty()) {
                AppUser hr = new AppUser();
                hr.setUsername("hr_manager");
                hr.setPassword(passwordEncoder.encode("hr123"));
                hr.setRole(UserRole.HR);
                userRepo.save(hr);
                System.out.println("✅ HR created — hr_manager");
            }

            // ── EMPLOYEES ────────────────────────────────────────────────────
            Employee emp1 = ensureEmployee(employeeRepo, passwordEncoder,
                    "emp1", "emp123", "Alice", "Insurance", UserRole.EMPLOYEE);

            Employee emp2 = ensureEmployee(employeeRepo, passwordEncoder,
                    "emp2", "emp456", "Bob", "Coverage", UserRole.EMPLOYEE);

            Employee rahul = ensureEmployee(employeeRepo, passwordEncoder,
                    "rahul", "rahul123", "Rahul", "Verma", UserRole.EMPLOYEE);

            // sneha intentionally gets NO insurance — summary 404 edge case
            ensureEmployee(employeeRepo, passwordEncoder,
                    "sneha", "sneha123", "Sneha", "Patel", UserRole.EMPLOYEE);

            // ── INSURANCE PLANS ──────────────────────────────────────────────
            InsurancePlan basicPlan = ensurePlan(insurancePlanRepo,
                    "Basic Health Plan", 500000.0,
                    "Covers hospitalisation and surgery", true);

            InsurancePlan premiumPlan = ensurePlan(insurancePlanRepo,
                    "Premium Health Plan", 1000000.0,
                    "Covers critical illness, surgery, and ICU", true);

            InsurancePlan inactivePlan = ensurePlan(insurancePlanRepo,
                    "Legacy Cover Plan", 200000.0,
                    "Discontinued — kept for audit", false);

            // ── TOP-UP PLANS ─────────────────────────────────────────────────
            TopUpPlan silverTopUp = ensureTopUp(topUpPlanRepo,
                    "Silver Top-Up", 200000.0, 1500.0, "Adds 2L coverage", true);

            TopUpPlan goldTopUp = ensureTopUp(topUpPlanRepo,
                    "Gold Top-Up", 500000.0, 3500.0, "Adds 5L coverage", true);

            ensureTopUp(topUpPlanRepo,
                    "Old Booster Top-Up", 100000.0, 500.0, "Discontinued", false);

            // ── EMPLOYEE INSURANCES ──────────────────────────────────────────
            EmployeeInsurance emp1Insurance = ensureInsurance(
                    employeeInsuranceRepo, emp1, basicPlan,
                    LocalDate.now().minusMonths(6),
                    LocalDate.now().plusMonths(6),
                    InsuranceStatus.ACTIVE);

            ensureInsurance(employeeInsuranceRepo, emp1, inactivePlan,
                    LocalDate.now().minusYears(2),
                    LocalDate.now().minusMonths(1),
                    InsuranceStatus.EXPIRED);

            EmployeeInsurance emp2Insurance = ensureInsurance(
                    employeeInsuranceRepo, emp2, premiumPlan,
                    LocalDate.now().minusMonths(3),
                    LocalDate.now().plusMonths(9),
                    InsuranceStatus.ACTIVE);

            // expiring in 25 days — hits /expiring-soon?days=30
            EmployeeInsurance rahulInsurance = ensureInsurance(
                    employeeInsuranceRepo, rahul, basicPlan,
                    LocalDate.now().minusMonths(11),
                    LocalDate.now().plusDays(25),
                    InsuranceStatus.ACTIVE);

            // ── EMPLOYEE TOP-UPS ─────────────────────────────────────────────
            ensureEmployeeTopUp(employeeTopUpRepo, emp1, silverTopUp,
                    LocalDate.now().plusMonths(6));

            ensureEmployeeTopUp(employeeTopUpRepo, rahul, goldTopUp,
                    LocalDate.now().plusMonths(4));
            // emp2 has NO top-up → /no-topup report

            // ── INSURANCE CLAIMS ─────────────────────────────────────────────
            // APPROVED — re-resolve should fail 4xx (Postman 4.8)
            ensureClaim(insuranceClaimRepo, emp1, emp1Insurance,
                    15000.0, "Hospitalisation for appendix surgery",
                    ClaimStatus.APPROVED,
                    LocalDateTime.now().minusDays(10),
                    LocalDateTime.now().minusDays(8),
                    ADMIN_USERNAME, "Verified with hospital invoice. Approved.");

            // REJECTED — set-back-to-PENDING should fail 4xx (Postman 4.9)
            ensureClaim(insuranceClaimRepo, emp1, emp1Insurance,
                    5000.0, "Dental treatment",
                    ClaimStatus.REJECTED,
                    LocalDateTime.now().minusDays(5),
                    LocalDateTime.now().minusDays(4),
                    ADMIN_USERNAME, "Dental not covered under Basic Health Plan.");

            // PENDING — pending-claims report + can be resolved via Postman
            ensureClaim(insuranceClaimRepo, rahul, rahulInsurance,
                    30000.0, "Emergency knee surgery",
                    ClaimStatus.PENDING,
                    LocalDateTime.now().minusDays(2),
                    null, null, null);

            // second PENDING — pagination test
            ensureClaim(insuranceClaimRepo, emp2, emp2Insurance,
                    12000.0, "Cataract operation",
                    ClaimStatus.PENDING,
                    LocalDateTime.now().minusDays(1),
                    null, null, null);

            System.out.println("✅ InsuranceDataLoader complete");
        };
    }

    private Employee ensureEmployee(EmployeeRepository repo, PasswordEncoder enc,
            String username, String pass, String first, String last, UserRole role) {
        return repo.findByUsername(username).orElseGet(() -> {
            Employee e = new Employee();
            e.setUsername(username);
            e.setPassword(enc.encode(pass));
            e.setRole(role);
            e.setFirstName(first);
            e.setLastName(last);
            Employee saved = repo.save(e);
            System.out.println("✅ Employee created — " + username);
            return saved;
        });
    }

    private InsurancePlan ensurePlan(InsurancePlanRepository repo,
            String name, Double coverage, String desc, Boolean active) {
        return repo.findByPlanName(name).orElseGet(() -> {
            InsurancePlan p = new InsurancePlan();
            p.setPlanName(name);
            p.setCoverageAmount(coverage);
            p.setDescription(desc);
            p.setIsActive(active);
            p.setCreatedBy(ADMIN_USERNAME);
            InsurancePlan saved = repo.save(p);
            System.out.println("InsurancePlan created — " + name);
            return saved;
        });
    }

    private TopUpPlan ensureTopUp(TopUpPlanRepository repo,
            String name, Double coverage, Double price, String desc, Boolean active) {
        return repo.findByTopUpName(name).orElseGet(() -> {
            TopUpPlan t = new TopUpPlan();
            t.setTopUpName(name);
            t.setAdditionalCoverage(coverage);
            t.setPrice(price);
            t.setDescription(desc);
            t.setIsActive(active);
            t.setCreatedBy(ADMIN_USERNAME);
            TopUpPlan saved = repo.save(t);
            System.out.println("TopUpPlan created — " + name);
            return saved;
        });
    }

    private EmployeeInsurance ensureInsurance(EmployeeInsuranceRepository repo,
            Employee emp, InsurancePlan plan,
            LocalDate assigned, LocalDate expiry, InsuranceStatus status) {
        return repo.findByEmployee_UserIdAndInsurancePlan_Id(emp.getUserId(), plan.getId())
                .orElseGet(() -> {
                    EmployeeInsurance ei = new EmployeeInsurance();
                    ei.setEmployee(emp);
                    ei.setInsurancePlan(plan);
                    ei.setAssignedDate(assigned);
                    ei.setExpiryDate(expiry);
                    ei.setStatus(status);
                    ei.setAssignedBy(ADMIN_USERNAME);
                    EmployeeInsurance saved = repo.save(ei);
                    System.out.println("✅ EmployeeInsurance — " + emp.getUsername()
                            + " → " + plan.getPlanName() + " [" + status + "]");
                    return saved;
                });
    }

    private void ensureEmployeeTopUp(EmployeeTopUpRepository repo,
            Employee emp, TopUpPlan plan, LocalDate expiry) {
        if (!repo.existsByEmployee_UserIdAndTopUpPlan_Id(emp.getUserId(), plan.getId())) {
            EmployeeTopUp t = new EmployeeTopUp();
            t.setEmployee(emp);
            t.setTopUpPlan(plan);
            t.setPurchasedDate(LocalDate.now());
            t.setExpiryDate(expiry);
            t.setStatus(InsuranceStatus.ACTIVE);
            repo.save(t);
            System.out.println("EmployeeTopUp — " + emp.getUsername()
                    + " → " + plan.getTopUpName());
        }
    }

    private void ensureClaim(InsuranceClaimRepository repo,
            Employee emp, EmployeeInsurance ins,
            Double amount, String reason, ClaimStatus status,
            LocalDateTime raisedAt, LocalDateTime resolvedAt,
            String resolvedBy, String remarks) {
        if (!repo.existsByEmployee_UserIdAndEmployeeInsurance_IdAndReason(
                emp.getUserId(), ins.getId(), reason)) {
            InsuranceClaim c = new InsuranceClaim();
            c.setEmployee(emp);
            c.setEmployeeInsurance(ins);
            c.setClaimAmount(amount);
            c.setReason(reason);
            c.setStatus(status);
            c.setRaisedAt(raisedAt != null ? raisedAt : LocalDateTime.now());
            c.setResolvedAt(resolvedAt);
            c.setResolvedBy(resolvedBy);
            c.setAdminRemarks(remarks);
            repo.save(c);
            System.out.println("Claim — " + emp.getUsername()
                    + " [" + status + "] " + reason);
        }
    }
}