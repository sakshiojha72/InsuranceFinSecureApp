package com.ds.app.repository;

import com.ds.app.entity.Company;
import com.ds.app.entity.Department;
import com.ds.app.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface iProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCompany(Company company);
    List<Project> findByDepartment(Department department);
    List<Project> findByStatus(String status);
}

