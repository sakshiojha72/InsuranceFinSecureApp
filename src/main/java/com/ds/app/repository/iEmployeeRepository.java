package com.ds.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ds.app.entity.Company;
import com.ds.app.entity.Department;
import com.ds.app.entity.Employee;

public interface iEmployeeRepository extends JpaRepository<Employee, Long>{
	
	
	
	//Non paginated for internal use 
	List<Employee> findByIsDeletedFalse();
	

	//count for reports 
	long countByCompany(Company company);
	long countByDepartment(Department department);
	long countByIsDeletedFalse();
	long countByIsEscalatedTrue();
	long countByIsCertifiedFalse();
	
	
	//Group by queries
	//returns [id,counts] pairs
	
	@Query("SELECT e.company.id, COUNT(e) FROM EMPLOYEE e"+
	"WHERE e.isDeleted=false AND e.company IS NOT NULL" +
			"GROUP BY e.company.id"
	)
	List<Object[]> countGroupByCompany();
	
	@Query("SELECT e.department.id, COUNT(e) From Employee e"+
	"WHERE e.isDeleted=false AND e.department IS NOT NULL"+
			"GROUP BY e.department.id")
	List<Object[]> countGroupByDepartment();
	
	@Query("SELECT e.employeeType, COUNT(e) From Employee e"+
			"WHERE e.isDeleted=false"+
			"GROUP BY e.employeeType")
	List<Object[]> countGroupByEmployeeType();
	
	@Query("SELECT e.status,COUNT(e) FROM EMPLOYEE e"+
	"WHERE e.isDeleted=false"+
	"GROUP BY e.status")
	
	List<Object[]> countGroupByStatus();

}
