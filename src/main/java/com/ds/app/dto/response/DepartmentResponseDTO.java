package com.ds.app.dto.response;

import lombok.Data;

@Data
public class DepartmentResponseDTO {

    private Long id;
    private String name;
    private String code;
    private String status;

    // plain IDs — caller fetches details separately if needed
    private Long companyId;
    private String companyName;  // included for convenience — avoids extra call
}
