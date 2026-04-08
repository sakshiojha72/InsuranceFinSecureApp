package com.ds.app.repository;

import com.ds.app.dto.response.CompanyDetailDTO;
import com.ds.app.entity.Company;
import com.ds.app.entity.Department;
import com.ds.app.entity.Employee;
import com.ds.app.entity.Project;
import com.ds.app.enums.CertificationStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface iEmployeeRepository extends JpaRepository<Employee, Long> {

	
	
	//helpers
	
	 boolean existsByFirstName(String firstName);
    // ── paginated filters ─────────────────────────────────────────────
    Page<Employee> findByIsDeletedFalse(Pageable pageable);
    Page<Employee> findByCompany(Company company, Pageable pageable);
    Page<Employee> findByDepartment(Department department, Pageable pageable);
    Page<Employee> findByProject(Project project, Pageable pageable);
    Page<Employee> findByIsEscalatedTrue(Pageable pageable);
    Page<Employee> findByProjectIsNullAndIsDeletedFalse(Pageable pageable);

    // ── non-paginated for internal use ────────────────────────────────
    List<Employee> findByIsDeletedFalse();

    // ── counts for reports ────────────────────────────────────────────
    long countByCompany(Company company);
    long countByDepartment(Department department);
    long countByIsDeletedFalse();
    long countByIsEscalatedTrue();

    long countByCertificationStatus(CertificationStatus status);

    // ── group by queries — returns [id, count] pairs ──────────────────
    @Query("SELECT e.company.id, COUNT(e) FROM Employee e " +
           "WHERE e.isDeleted = false AND e.company IS NOT NULL " +
           "GROUP BY e.company.id")
    List<Object[]> countGroupByCompany();

    @Query("SELECT e.department.id, COUNT(e) FROM Employee e " +
           "WHERE e.isDeleted = false AND e.department IS NOT NULL " +
           "GROUP BY e.department.id")
    List<Object[]> countGroupByDepartment();

    @Query("SELECT e.employeeExperience, COUNT(e) FROM Employee e " +
           "WHERE e.isDeleted = false " +
           "GROUP BY e.employeeExperience")
    List<Object[]> countGroupByEmployeeType();

    @Query("SELECT e.status, COUNT(e) FROM Employee e " +
           "WHERE e.isDeleted = false " +
           "GROUP BY e.status")
    List<Object[]> countGroupByStatus();
    
    
    @Query("SELECT new com.ds.app.dto.response.CompanyDetailDTO(" +
    	       "c.name, " + 
    	       "d.name, " + 
    	       "CONCAT(e.firstName, ' ', e.lastName), " + // Using first + last name
    	       "p.name) " + 
    	       "FROM Employee e " +
    	       "JOIN e.company c " +
    	       "JOIN e.department d " +
    	       "LEFT JOIN e.project p " + // Use 'project' (singular) as per your entity
    	       "WHERE e.isDeleted = false")
    	List<CompanyDetailDTO> getDetailedCompanyReport();

 }

