package com.ds.app.repository;

import com.ds.app.entity.Company;
import com.ds.app.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface iDepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByCompany(Company company);
    boolean existsByCodeAndCompany(String code, Company company);
}
