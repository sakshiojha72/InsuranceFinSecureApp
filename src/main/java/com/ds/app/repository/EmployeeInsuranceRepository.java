package com.ds.app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ds.app.entity.EmployeeInsurance;
import com.ds.app.entity.InsuranceStatus;

@Repository
public interface EmployeeInsuranceRepository extends JpaRepository<EmployeeInsurance, Long> {

    Optional<EmployeeInsurance> findByEmployee_UserIdAndStatus(Long employeeId, InsuranceStatus active);

    List<EmployeeInsurance> findByEmployee_UserId(Long employeeId);

    boolean existsByEmployee_UserIdAndStatus(Long employeeId, InsuranceStatus active);

    // Query 1 — with-topup report
    // IN :statuses so both ACTIVE and EXPIRING_SOON employees are included
    @Query("SELECT DISTINCT ei FROM EmployeeInsurance ei " +
           "JOIN EmployeeTopUp et ON et.employee = ei.employee " +
           "WHERE ei.status IN :statuses")
    List<EmployeeInsurance> findEmployeesWithActiveInsuranceAndTopUp(
            @Param("statuses") List<InsuranceStatus> statuses);

    // Query 2 — no-topup report
    // IN :statuses so EXPIRING_SOON employees without top-ups are also shown
    @Query("SELECT ei FROM EmployeeInsurance ei " +
           "WHERE ei.status IN :statuses " +
           "AND NOT EXISTS (" +
           "    SELECT et FROM EmployeeTopUp et " +
           "    WHERE et.employee = ei.employee" +
           ")")
    List<EmployeeInsurance> findEmployeesWithInsuranceButNoTopUp(
            @Param("statuses") List<InsuranceStatus> statuses);

    // Query 3 — assigned-between and financial-year reports
    // No status filter — show ALL assignments in the date range regardless of status
    @Query("SELECT ei FROM EmployeeInsurance ei " +
           "WHERE ei.assignedDate BETWEEN :startDate AND :endDate")
    List<EmployeeInsurance> findInsurancesAssignedBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // Query 5 — expiring-soon report
    // IN :statuses — EXPIRING_SOON records must appear here too
    @Query("SELECT ei FROM EmployeeInsurance ei " +
           "WHERE ei.status IN :statuses " +
           "AND ei.expiryDate BETWEEN :today AND :alertDate")
    List<EmployeeInsurance> findInsurancesExpiringBetween(
            @Param("statuses") List<InsuranceStatus> statuses,
            @Param("today") LocalDate today,
            @Param("alertDate") LocalDate alertDate);

    Optional<EmployeeInsurance> findByEmployee_UserIdAndInsurancePlan_Id(Long userId, Long planId);

    List<EmployeeInsurance> findByInsurancePlan_IdAndStatusIn(
         Long planId, List<InsuranceStatus> statuses);
    
    List<EmployeeInsurance> findAllByEmployee_UserIdAndInsurancePlan_Id(Long userId, Long planId);
}