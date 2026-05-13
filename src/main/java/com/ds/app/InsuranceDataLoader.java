package com.ds.app;

import com.ds.app.entity.*;
import com.ds.app.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class InsuranceDataLoader {

    private static final String ADMIN1 = "admin1";
    private static final String ADMIN2 = "admin2";

    @Bean
    CommandLineRunner seedInsuranceData(
            iAppUserRepository      userRepo,
            EmployeeRepository      employeeRepo,
            InsurancePlanRepository planRepo,
            EmployeeInsuranceRepository empInsuranceRepo,
            TopUpPlanRepository     topUpPlanRepo,
            EmployeeTopUpRepository empTopUpRepo,
            InsuranceClaimRepository claimRepo,
            PasswordEncoder         encoder) {

        return args -> {

            // ── ADMINS ───────────────────────────────────────────────────────
            AppUser admin1 = saveAdmin(userRepo, encoder, ADMIN1, "admin123");
            saveAdmin(userRepo, encoder, ADMIN2, "admin456");

            // ── HR ───────────────────────────────────────────────────────────
            saveHR(userRepo, encoder, "hr_riya",  "hr123");
            saveHR(userRepo, encoder, "hr_karan", "hr456");

            // ── FINANCE ──────────────────────────────────────────────────────
            saveFinance(userRepo, encoder, "finance_neha", "fin123");

            // ── EMPLOYEES ────────────────────────────────────────────────────
            Employee sakshi  = saveEmployee(employeeRepo, encoder,
                    "sakshi",  "sakshi123",  "Sakshi",  "Ojha",   "sakshiojha72@gmail.com");
            Employee tarushi = saveEmployee(employeeRepo, encoder,
                    "tarushi", "tarushi123", "Tarushi", "Sharma", "sakshi.mca24@bvicam.in");
            Employee yatin   = saveEmployee(employeeRepo, encoder,
                    "yatin",   "yatin123",   "Yatin",   "Gupta",  "yatin@finsecure.com");
            Employee rahul   = saveEmployee(employeeRepo, encoder,
                    "rahul",   "rahul123",   "Rahul",   "Verma",  "rahul@finsecure.com");
            Employee yash    = saveEmployee(employeeRepo, encoder,
                    "yash",    "yash123",    "Yash",    "Mishra", "yash@finsecure.com");
            Employee kalyani = saveEmployee(employeeRepo, encoder,
                    "kalyani", "kalyani123", "Kalyani", "Singh",  "kalyani@finsecure.com");
            Employee priya   = saveEmployee(employeeRepo, encoder,
                    "priya",   "priya123",   "Priya",   "Kapoor", "priya@finsecure.com");
            Employee deepak  = saveEmployee(employeeRepo, encoder,
                    "deepak",  "deepak123",  "Deepak",  "Nair",   "deepak@finsecure.com");
            Employee sneha   = saveEmployee(employeeRepo, encoder,
                    "sneha",   "sneha123",   "Sneha",   "Reddy",  "sneha@finsecure.com");
            Employee arjun   = saveEmployee(employeeRepo, encoder,
                    "arjun",   "arjun123",   "Arjun",   "Mehta",  "arjun@finsecure.com");

            // ── INSURANCE PLANS ──────────────────────────────────────────────
            // isDefault=true on Basic — this is the fallback plan for auto-reassignment
            InsurancePlan basic    = savePlan(planRepo,
                    "Basic Health Plan",   500000.0,
                    "Covers hospitalisation, surgery, and day-care procedures",
                    true, true, ADMIN1);   // ← isDefault = true

            InsurancePlan premium  = savePlan(planRepo,
                    "Premium Health Plan", 1000000.0,
                    "Covers critical illness, surgery, ICU, and organ transplant",
                    true, false, ADMIN1);

            InsurancePlan standard = savePlan(planRepo,
                    "Standard Care Plan",  300000.0,
                    "Covers OPD, diagnostics and minor surgery",
                    true, false, ADMIN1);

            // Inactive — retained for audit trail, must NOT appear in dropdowns
            savePlan(planRepo,
                    "Legacy Cover Plan",   200000.0,
                    "Discontinued plan retained for audit trail",
                    false, false, ADMIN1);

            // ── TOP-UP PLANS ─────────────────────────────────────────────────
            TopUpPlan silver = saveTopUp(topUpPlanRepo,
                    "Silver Top-Up",   200000.0, 1500.0,
                    "Adds ₹2L extra coverage", true, ADMIN1);
            TopUpPlan gold   = saveTopUp(topUpPlanRepo,
                    "Gold Top-Up",     500000.0, 3500.0,
                    "Adds ₹5L extra coverage", true, ADMIN1);
            saveTopUp(topUpPlanRepo,
                    "Platinum Top-Up", 800000.0, 5000.0,
                    "Adds ₹8L extra coverage", true, ADMIN1);
            saveTopUp(topUpPlanRepo,
                    "Old Booster",     100000.0,  500.0,
                    "Discontinued top-up plan", false, ADMIN1);

            // ── EMPLOYEE INSURANCES ──────────────────────────────────────────
            EmployeeInsurance sakshiIns  = saveInsurance(empInsuranceRepo,
                    sakshi,  basic,
                    LocalDate.now().minusMonths(3),  LocalDate.now().plusMonths(9),
                    InsuranceStatus.ACTIVE, ADMIN1);
            EmployeeInsurance tarushiIns = saveInsurance(empInsuranceRepo,
                    tarushi, premium,
                    LocalDate.now().minusMonths(1),  LocalDate.now().plusMonths(11),
                    InsuranceStatus.ACTIVE, ADMIN1);
            EmployeeInsurance yatinIns   = saveInsurance(empInsuranceRepo,
                    yatin,   basic,
                    LocalDate.now().minusMonths(11), LocalDate.now().plusDays(20),
                    InsuranceStatus.ACTIVE, ADMIN1);
            EmployeeInsurance rahulIns   = saveInsurance(empInsuranceRepo,
                    rahul,   basic,
                    LocalDate.now().minusYears(1),   LocalDate.now().minusDays(10),
                    InsuranceStatus.EXPIRED, ADMIN1);
            EmployeeInsurance priyaIns   = saveInsurance(empInsuranceRepo,
                    priya,   basic,
                    LocalDate.now().minusMonths(6),  LocalDate.now().plusMonths(6),
                    InsuranceStatus.ACTIVE, ADMIN2);
            EmployeeInsurance deepakIns  = saveInsurance(empInsuranceRepo,
                    deepak,  premium,
                    LocalDate.now().minusMonths(2),  LocalDate.now().plusMonths(10),
                    InsuranceStatus.ACTIVE, ADMIN1);
            EmployeeInsurance snehaIns   = saveInsurance(empInsuranceRepo,
                    sneha,   basic,
                    LocalDate.now().minusMonths(4),  LocalDate.now().plusMonths(8),
                    InsuranceStatus.ACTIVE, ADMIN2);
            EmployeeInsurance arjunIns   = saveInsurance(empInsuranceRepo,
                    arjun,   basic,
                    LocalDate.now().minusMonths(11).minusDays(20), LocalDate.now().plusDays(7),
                    InsuranceStatus.ACTIVE, ADMIN1);
            // yash and kalyani intentionally have no insurance

            // ── TOP-UP PURCHASES ─────────────────────────────────────────────
            saveTopUpPurchase(empTopUpRepo, empInsuranceRepo,
                    sakshi,  sakshiIns,  silver, LocalDate.now().plusMonths(9));
            saveTopUpPurchase(empTopUpRepo, empInsuranceRepo,
                    yatin,   yatinIns,   gold,   LocalDate.now().plusDays(20));
            saveTopUpPurchase(empTopUpRepo, empInsuranceRepo,
                    priya,   priyaIns,   silver, LocalDate.now().plusMonths(6));
            saveTopUpPurchase(empTopUpRepo, empInsuranceRepo,
                    deepak,  deepakIns,  gold,   LocalDate.now().plusMonths(10));

            // ── CLAIMS ───────────────────────────────────────────────────────
            // sakshi → APPROVED 50k  | remaining: 700k - 50k = 650k
            saveClaim(claimRepo, empInsuranceRepo, sakshi, sakshiIns,
                    50000.0, "Hospitalisation for appendix surgery",
                    ClaimStatus.APPROVED,
                    LocalDateTime.now().minusDays(15), LocalDateTime.now().minusDays(13),
                    ADMIN1, "Verified with hospital invoice. Approved.", true);

            // tarushi → REJECTED (dental not covered) | remaining: 1000k unchanged
            saveClaim(claimRepo, empInsuranceRepo, tarushi, tarushiIns,
                    20000.0, "Dental treatment — teeth whitening and root canal",
                    ClaimStatus.REJECTED,
                    LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4),
                    ADMIN2, "Dental cosmetic procedures are not covered under this plan.", false);

            // yatin → PENDING (no deduction until approved) | remaining: 1000k
            saveClaim(claimRepo, empInsuranceRepo, yatin, yatinIns,
                    30000.0, "Emergency knee surgery and physiotherapy",
                    ClaimStatus.PENDING,
                    LocalDateTime.now().minusDays(1), null, null, null, false);

            // priya → 3 claims: APPROVED 80k + PENDING + REJECTED | remaining: 700k - 80k = 620k
            saveClaim(claimRepo, empInsuranceRepo, priya, priyaIns,
                    80000.0, "Maternity hospitalisation and post-natal care",
                    ClaimStatus.APPROVED,
                    LocalDateTime.now().minusDays(45), LocalDateTime.now().minusDays(42),
                    ADMIN1, "All documents verified. Maternity covered. Approved.", true);
            saveClaim(claimRepo, empInsuranceRepo, priya, priyaIns,
                    15000.0, "Diagnostic tests — MRI and blood panel",
                    ClaimStatus.PENDING,
                    LocalDateTime.now().minusHours(6), null, null, null, false);
            saveClaim(claimRepo, empInsuranceRepo, priya, priyaIns,
                    8000.0, "Annual gym membership fee",
                    ClaimStatus.REJECTED,
                    LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(19),
                    ADMIN2, "Gym membership is not a medical expense. Claim rejected.", false);

            // deepak → APPROVED 300k | remaining: 1500k - 300k = 1200k
            saveClaim(claimRepo, empInsuranceRepo, deepak, deepakIns,
                    300000.0, "Cardiac bypass surgery and post-operative ICU stay",
                    ClaimStatus.APPROVED,
                    LocalDateTime.now().minusDays(30), LocalDateTime.now().minusDays(28),
                    ADMIN1, "Cardiac surgery fully covered under premium plan. Approved.", true);

            // sneha → APPROVED 20k | remaining: 500k - 20k = 480k
            saveClaim(claimRepo, empInsuranceRepo, sneha, snehaIns,
                    20000.0, "Day-care procedure — cataract surgery",
                    ClaimStatus.APPROVED,
                    LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(9),
                    ADMIN1, "Day-care procedure covered. Approved.", true);

            // arjun → no claims (expiring-soon focus)

            System.out.println("=================================================");
            System.out.println("  InsuranceDataLoader — ALL SCENARIOS SEEDED     ");
            System.out.println("=================================================");
            System.out.println("  ADMINS  : admin1/admin123  admin2/admin456");
            System.out.println("  HR      : hr_riya/hr123    hr_karan/hr456");
            System.out.println("  FINANCE : finance_neha/fin123");
            System.out.println("-------------------------------------------------");
            System.out.println("  sakshi  → ACTIVE + silver topup + APPROVED      → 650 000");
            System.out.println("  tarushi → ACTIVE premium + REJECTED              → 1 000 000");
            System.out.println("  yatin   → ACTIVE (exp 20d) + gold + PENDING      → 1 000 000");
            System.out.println("  rahul   → EXPIRED (admin must renew)");
            System.out.println("  yash    → NO insurance");
            System.out.println("  kalyani → NO insurance");
            System.out.println("  priya   → ACTIVE + silver + 3 claims (A/P/R)    → 620 000");
            System.out.println("  deepak  → ACTIVE premium + gold + APPROVED large → 1 200 000");
            System.out.println("  sneha   → ACTIVE basic + APPROVED small          → 480 000");
            System.out.println("  arjun   → ACTIVE (exp 7d!) + no claims");
            System.out.println("  DEFAULT PLAN: Basic Health Plan");
            System.out.println("=================================================");
        };
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private AppUser saveAdmin(iAppUserRepository repo,
            PasswordEncoder enc, String username, String pass) {
        return repo.findByUsername(username).orElseGet(() -> {
            AppUser u = new AppUser();
            u.setUsername(username);
            u.setPassword(enc.encode(pass));
            u.setRole(UserRole.ADMIN);
            return repo.save(u);
        });
    }

    private void saveHR(iAppUserRepository repo,
            PasswordEncoder enc, String username, String pass) {
        repo.findByUsername(username).orElseGet(() -> {
            AppUser u = new AppUser();
            u.setUsername(username);
            u.setPassword(enc.encode(pass));
            u.setRole(UserRole.HR);
            return repo.save(u);
        });
    }

    private void saveFinance(iAppUserRepository repo,
            PasswordEncoder enc, String username, String pass) {
        repo.findByUsername(username).orElseGet(() -> {
            AppUser u = new AppUser();
            u.setUsername(username);
            u.setPassword(enc.encode(pass));
            u.setRole(UserRole.FINANCE);
            return repo.save(u);
        });
    }

    private Employee saveEmployee(EmployeeRepository repo,
            PasswordEncoder enc,
            String username, String pass,
            String first, String last, String email) {
        return repo.findByEmail(email).orElseGet(() -> {
            Employee e = new Employee();
            e.setUsername(username);
            e.setPassword(enc.encode(pass));
            e.setRole(UserRole.EMPLOYEE);
            e.setFirstName(first);
            e.setLastName(last);
            e.setEmail(email);
            return repo.save(e);
        });
    }

    // CHANGED — added isDefault parameter
    private InsurancePlan savePlan(InsurancePlanRepository repo,
            String name, Double coverage, String desc,
            Boolean active, Boolean isDefault, String createdBy) {
        return repo.findByPlanName(name).orElseGet(() -> {
            InsurancePlan p = new InsurancePlan();
            p.setPlanName(name);
            p.setCoverageAmount(coverage);
            p.setDescription(desc);
            p.setIsActive(active);
            p.setIsDefault(isDefault); // ← new field set here
            p.setCreatedBy(createdBy);
            return repo.save(p);
        });
    }

    private TopUpPlan saveTopUp(TopUpPlanRepository repo,
            String name, Double coverage, Double price,
            String desc, Boolean active, String createdBy) {
        return repo.findByTopUpName(name).orElseGet(() -> {
            TopUpPlan t = new TopUpPlan();
            t.setTopUpName(name);
            t.setAdditionalCoverage(coverage);
            t.setPrice(price);
            t.setDescription(desc);
            t.setIsActive(active);
            t.setCreatedBy(createdBy);
            return repo.save(t);
        });
    }

    private EmployeeInsurance saveInsurance(EmployeeInsuranceRepository repo,
            Employee emp, InsurancePlan plan,
            LocalDate assigned, LocalDate expiry,
            InsuranceStatus status, String assignedBy) {

        List<EmployeeInsurance> existing =
                repo.findAllByEmployee_UserIdAndInsurancePlan_Id(
                        emp.getUserId(), plan.getId());
        if (!existing.isEmpty()) return existing.get(existing.size() - 1);

        EmployeeInsurance ei = new EmployeeInsurance();
        ei.setEmployee(emp);
        ei.setInsurancePlan(plan);
        ei.setAssignedDate(assigned);
        ei.setExpiryDate(expiry);
        ei.setStatus(status);
        ei.setAssignedBy(assignedBy);
        ei.setRemainingCoverage(plan.getCoverageAmount());
        ei.setBaseAmount(plan.getCoverageAmount());
        return repo.save(ei);
    }

    private void saveTopUpPurchase(EmployeeTopUpRepository topUpRepo,
            EmployeeInsuranceRepository insRepo,
            Employee emp, EmployeeInsurance ins,
            TopUpPlan plan, LocalDate expiry) {

        if (topUpRepo.existsByEmployee_UserIdAndTopUpPlan_Id(emp.getUserId(), plan.getId())) return;

        EmployeeTopUp t = new EmployeeTopUp();
        t.setEmployee(emp);
        t.setTopUpPlan(plan);
        t.setPurchasedDate(LocalDate.now());
        t.setExpiryDate(expiry);
        t.setStatus(InsuranceStatus.ACTIVE);
        topUpRepo.save(t);

        ins.setRemainingCoverage(ins.getRemainingCoverage() + plan.getAdditionalCoverage());
        insRepo.save(ins);
    }

    private void saveClaim(InsuranceClaimRepository claimRepo,
            EmployeeInsuranceRepository insRepo,
            Employee emp, EmployeeInsurance ins,
            Double amount, String reason, ClaimStatus status,
            LocalDateTime raisedAt, LocalDateTime resolvedAt,
            String resolvedBy, String remarks,
            boolean deductCoverage) {

        if (claimRepo.existsByEmployee_UserIdAndEmployeeInsurance_IdAndReason(
                emp.getUserId(), ins.getId(), reason)) return;

        InsuranceClaim c = new InsuranceClaim();
        c.setEmployee(emp);
        c.setEmployeeInsurance(ins);
        c.setClaimAmount(amount);
        c.setBaseAmount(null);
        c.setReason(reason);
        c.setStatus(status);
        c.setRaisedAt(raisedAt != null ? raisedAt : LocalDateTime.now());
        c.setResolvedAt(resolvedAt);
        c.setResolvedBy(resolvedBy);
        c.setAdminRemarks(remarks);
        claimRepo.save(c);

        // deduct only on APPROVED claims — pending/rejected don't reduce coverage
        if (deductCoverage) {
            ins.setRemainingCoverage(ins.getRemainingCoverage() - amount);
            insRepo.save(ins);
        }
    }
}