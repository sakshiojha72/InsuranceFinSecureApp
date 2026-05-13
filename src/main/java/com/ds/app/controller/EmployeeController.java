package com.ds.app.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.app.entity.Employee;
import com.ds.app.repository.EmployeeRepository;

@RestController
@RequestMapping("/finsecure/employee")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "successfuly accessing employee controller";
    }

    @GetMapping("/employees")
    public List<EmployeeListDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(emp -> new EmployeeListDTO(
                        emp.getUserId(),
                        emp.getFirstName(),
                        emp.getLastName(),
                        emp.getEmail(),
                        emp.getUsername()
                ))
                .toList();
    }

    public static class EmployeeListDTO {
        private Long userId;
        private String firstName;
        private String lastName;
        private String email;
        private String username;

        public EmployeeListDTO(Long userId, String firstName, String lastName, String email, String username) {
            this.userId = userId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.username = username;
        }

        public Long getUserId() {
            return userId;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }
    }
}