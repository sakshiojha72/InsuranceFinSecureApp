package com.ds.app.dto.response;

import lombok.Data;


@Data
public class CompanyResponseDTO {

    private Long id;
    private String name;
    private String code;
    private Boolean restrictsInvestment;
    private String status;
}
