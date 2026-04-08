package com.ds.app.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ds.app.entity.Employee;
import com.ds.app.service.EmployeeServiceImpl;


@Component
public class AssetsServiceClient {

  @Autowired EmployeeServiceImpl employeeService;
    
    public boolean hasNoOpenIssues(Long employeeId) {
//        Employee emp = employeeService.findOrThrow(employeeId);
//
//        // hasActiveAssetEscalation = true means there IS an open issue
//        // we return true only when there are NO open issues
//        return !Boolean.TRUE.equals(emp.getHasActiveAssetEscalation());
    	
    	
    	return true;
    }
	
	

}
