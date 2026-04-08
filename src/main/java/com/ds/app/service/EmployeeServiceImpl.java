package com.ds.app.service;


import com.ds.app.dto.request.EmployeeRequestDTO;
import com.ds.app.dto.response.EmployeeResponseDTO;
import com.ds.app.entity.Employee;
import com.ds.app.exception.HrException;
import com.ds.app.exception.HrResourceNotFoundException;
import com.ds.app.repository.iEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl {

	@Autowired
	private iEmployeeRepository employeeRepo;
	@Autowired
	private CompanyServiceImpl companyService;
	@Autowired
	private DepartmentServiceImpl departmentService;
	@Autowired
	private ProjectServiceImpl projectService;

	// Entity -> Response DTO
	public EmployeeResponseDTO toResponse(Employee emp) {
		EmployeeResponseDTO res = new EmployeeResponseDTO();
		res.setUserId(emp.getUserId());
		res.setUsername(emp.getUsername());
		res.setRole(emp.getRole() != null ? emp.getRole().toString() : null);
		res.setEmployeeCode(emp.getEmployeeCode());
		res.setEmployeeExperience(emp.getEmployeeExperience());
		res.setCompanyId(emp.getCompanyId());
		res.setDepartmentId(emp.getDepartmentId());
		res.setProjectId(emp.getProjectId());
		res.setCertificationStatus(emp.getCertificationStatus());
		res.setIsEscalated(emp.getIsEscalated());
		res.setCurrentSalary(emp.getCurrentSalary());
		res.setJoiningDate(emp.getJoiningDate());
		res.setStatus(emp.getStatus());
		return res;
	}

	// get all employees paginated
	public Map<String, Object> getAllEmployees(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("joiningDate").descending());
		return buildPage(employeeRepo.findByIsDeletedFalse(pageable));

	}

	// get one Employee by userId
	public EmployeeResponseDTO getById(Long userId) {
		Employee emp = findOrThrow(userId);
		return toResponse(emp);
	}

	// update Employee Profile(HR)
	public EmployeeResponseDTO updateEmployee(Long userId, EmployeeRequestDTO req) {
		Employee emp = findOrThrow(userId);
		if (req.getEmployeeCode() != null)
			emp.setEmployeeCode(req.getEmployeeCode());
		if (req.getEmployeeExperience() != null)
			emp.setEmployeeExperience(req.getEmployeeExperience());
		if (req.getCertificationStatus() != null)
			emp.setCertificationStatus(null);
		if (req.getCurrentSalary() != null)
			emp.setCurrentSalary(req.getCurrentSalary());
		if (req.getJoiningDate() != null)
			emp.setJoiningDate(req.getJoiningDate());
		if (req.getStatus() != null)
			emp.setStatus(req.getStatus());
		return toResponse(employeeRepo.save(emp));
	}

	// soft Delete
	public String softDelete(Long userId) {
		Employee emp = findOrThrow(userId);
		emp.setIsDeleted(true);
		employeeRepo.save(emp);
		return "Employee deleted successfully";
	}

	// filters paginated

	public Map<String,Object> getByCompany(Long companyId,int page,int size){
		 return buildPage(employeeRepo.findByCompany(companyService.findOrThrow(companyId), null));
	 }

	public Map<String, Object> getByDepartment(Long deptId, int page, int size) {
		return buildPage(
				employeeRepo.findByDepartment(departmentService.findOrThrow(deptId), PageRequest.of(page, size)));
	}

	public Map<String, Object> getByProject(Long projectId, int page, int size) {
		return buildPage(employeeRepo.findByProject(projectService.findOrThrow(projectId), PageRequest.of(page, size)));
	}

	public Map<String, Object> getEscalated(int page, int size) {
		return buildPage(employeeRepo.findByIsEscalatedTrue(PageRequest.of(page, size)));
	}

	public Map<String, Object> getUnassigned(int page, int size) {
		return buildPage(employeeRepo.findByProjectIsNullAndIsDeletedFalse(PageRequest.of(page, size)));
	}

	// COUNT helpers for reports
	public long countByCompany(Long companyId) {
		return employeeRepo.countByCompany(companyService.findOrThrow(companyId));
	}

	public long countByDepartment(Long deptId) {
		return employeeRepo.countByDepartment(departmentService.findOrThrow(deptId));
	}

	// INTERNAL HELPERS
	public Map<String, Object> buildPage(Page<Employee> page) {
		Map<String, Object> res = new LinkedHashMap<>();
		res.put("content", page.getContent().stream().map(this::toResponse).collect(Collectors.toList()));
		res.put("currentPage", page.getNumber());
		res.put("totalItems", page.getTotalElements());
		res.put("totalPages", page.getTotalPages());
		res.put("isLast", page.isLast());
		return res;
	}

	public Employee findOrThrow(Long userId) {
		return employeeRepo.findById(userId).orElseThrow(() -> new HrResourceNotFoundException("Employee" , userId));
	}

	

}
