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

    // find active insurance for an emp
    Optional<EmployeeInsurance> findByEmployee_UserIdAndStatus(Long employeeId, InsuranceStatus active);

    // get all insurance records (active + expired)
    List<EmployeeInsurance> findByEmployee_UserId(Long employeeId);

    // check if emp has an active insurance (for assigning new insurance)
    boolean existsByEmployee_UserIdAndStatus(Long employeeId, InsuranceStatus active);

    // Query 1 — active insurance AND active top-up
    // changed from Page to List, removed Pageable
    @Query("SELECT DISTINCT ei FROM EmployeeInsurance ei " +
           "JOIN EmployeeTopUp et ON et.employee = ei.employee " +
           "WHERE ei.status = :insuranceStatus " +
           "AND et.status = :topUpStatus")
    List<EmployeeInsurance> findEmployeesWithActiveInsuranceAndTopUp(
            @Param("insuranceStatus") InsuranceStatus insuranceStatus,
            @Param("topUpStatus") InsuranceStatus topUpStatus);

    // Query 2 — active insurance but NO top-up at all
    @Query("SELECT ei FROM EmployeeInsurance ei " +
           "WHERE ei.status = :status " +
           "AND NOT EXISTS (" +
           "    SELECT et FROM EmployeeTopUp et " +
           "    WHERE et.employee = ei.employee" +
           ")")
    List<EmployeeInsurance> findEmployeesWithInsuranceButNoTopUp(
            @Param("status") InsuranceStatus status);

    // Query 3 — assigned between two dates
    @Query("SELECT ei FROM EmployeeInsurance ei " +
           "WHERE ei.assignedDate BETWEEN :startDate AND :endDate " +
           "AND ei.status = :status")
    List<EmployeeInsurance> findInsurancesAssignedBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") InsuranceStatus status);

    // Query 5 — expiring between today and alertDate
    // changed from Page to List, removed Pageable
    @Query("SELECT ei FROM EmployeeInsurance ei " +
           "WHERE ei.status = :status " +
           "AND ei.expiryDate BETWEEN :today AND :alertDate")
    List<EmployeeInsurance> findInsurancesExpiringBetween(
            @Param("status") InsuranceStatus status,
            @Param("today") LocalDate today,
            @Param("alertDate") LocalDate alertDate);
}