package com.ds.app.service;

import com.ds.app.dto.response.InsuranceSummaryDTO;

public interface InsuranceSummaryService {
	// returns full insurance picture for an employee
    // base plan + all active top-ups + total coverage calculated
    InsuranceSummaryDTO getInsuranceSummary(Long employeeId);


}
