package com.ds.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ds.app.entity.AppUser;
import com.ds.app.entity.Employee;
import com.ds.app.entity.UserRole;
import com.ds.app.repository.iAppUserRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.service.AppUserService;
@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private iAppUserRepository appUserRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public AppUser registerAppUser(AppUser user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == UserRole.EMPLOYEE) {

            Employee employee = new Employee();
            employee.setUsername(user.getUsername());
            employee.setPassword(user.getPassword());
            employee.setRole(user.getRole());

            employee.setFailedLoginAttemptsCount(0);
            employee.setIsAccountLocked(false);
            employee.setFirstName("");
            employee.setLastName("");

            return employeeRepository.save(employee);
        }

        return appUserRepository.save(user);
    }
}