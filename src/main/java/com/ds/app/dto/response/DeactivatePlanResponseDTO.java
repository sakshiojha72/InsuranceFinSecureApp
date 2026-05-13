package com.ds.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeactivatePlanResponseDTO {

    private String deactivatedPlanName;       
    private String defaultPlanAssigned;       
    private int affectedEmployeeCount;        
    private List<String> affectedEmployeeNames; 
    private String message;                  
}