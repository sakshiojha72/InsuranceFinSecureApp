package com.ds.app;

import com.ds.app.dto.request.AssignInsuranceRequestDTO;
import com.ds.app.dto.request.CreateInsurancePlanRequestDTO;
import com.ds.app.dto.response.EmployeeInsuranceResponseDTO;
import com.ds.app.dto.response.InsurancePlanResponseDTO;
import com.ds.app.entity.*;
import com.ds.app.repository.EmployeeInsuranceRepository;
import com.ds.app.repository.EmployeeRepository;
import com.ds.app.repository.InsurancePlanRepository;
import com.ds.app.service.impl.InsurancePlanServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsurancePlanServiceImplTest {

    // --- MOCKS ---
    @Mock
    private InsurancePlanRepository insurancePlanRepository;

    @Mock
    private EmployeeInsuranceRepository employeeInsuranceRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    // --- REAL service with mocks injected ---
    @InjectMocks
    private InsurancePlanServiceImpl insurancePlanService;


    // TEST 1: createInsurancePlan — happy path
    @Test
    void createInsurancePlan_ShouldReturnResponse_WhenPlanNameIsUnique() {

        // ARRANGE — set up the fake data and fake repository behavior

        CreateInsurancePlanRequestDTO dto = new CreateInsurancePlanRequestDTO();
        dto.setPlanName("Gold Plan");
        dto.setCoverageAmount(500000.0);
        dto.setDescription("Premium plan");

        // fake: no plan with this name exists yet
        when(insurancePlanRepository.existsByPlanName("Gold Plan"))
                .thenReturn(false);

        // fake: when save() is called, return a plan with an ID
        InsurancePlan savedPlan = new InsurancePlan();
        savedPlan.setId(1L);
        savedPlan.setPlanName("Gold Plan");
        savedPlan.setCoverageAmount(500000.0);
        savedPlan.setDescription("Premium plan");
        savedPlan.setCreatedBy("admin01");
        savedPlan.setIsActive(true);

        when(insurancePlanRepository.save(any(InsurancePlan.class)))
                .thenReturn(savedPlan);

        // ACT — call the real service method
        InsurancePlanResponseDTO result =
                insurancePlanService.createInsurancePlan(dto, "admin01");

        // ASSERT — check the result is correct
        assertNotNull(result);
        assertEquals("Gold Plan", result.getPlanName());
        assertEquals(500000.0, result.getCoverageAmount());
        assertEquals("admin01", result.getCreatedBy());
        assertEquals(1L, result.getPlanId());
    }


    // TEST 2: createInsurancePlan — duplicate plan name
    @Test
    void createInsurancePlan_ShouldThrowException_WhenPlanNameExists() {

        CreateInsurancePlanRequestDTO dto = new CreateInsurancePlanRequestDTO();
        dto.setPlanName("Gold Plan");

        // fake: plan with this name ALREADY exists
        when(insurancePlanRepository.existsByPlanName("Gold Plan"))
                .thenReturn(true);

        // ACT + ASSERT — expect RuntimeException to be thrown
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                insurancePlanService.createInsurancePlan(dto, "admin01")
        );

        assertTrue(ex.getMessage().contains("already exists"));

        // verify save() was NEVER called (because we threw early)
        verify(insurancePlanRepository, never()).save(any());
    }


    // TEST 3: getAllInsurancePlans — returns only active plans
    @Test
    void getAllInsurancePlans_ShouldReturnOnlyActivePlans() {

        InsurancePlan plan1 = new InsurancePlan();
        plan1.setId(1L);
        plan1.setPlanName("Silver Plan");
        plan1.setCoverageAmount(200000.0);
        plan1.setIsActive(true);

        InsurancePlan plan2 = new InsurancePlan();
        plan2.setId(2L);
        plan2.setPlanName("Bronze Plan");
        plan2.setCoverageAmount(100000.0);
        plan2.setIsActive(true);

        when(insurancePlanRepository.findByIsActiveTrue())
                .thenReturn(List.of(plan1, plan2));

        List<InsurancePlanResponseDTO> result =
                insurancePlanService.getAllInsurancePlans();

        assertEquals(2, result.size());
        assertEquals("Silver Plan", result.get(0).getPlanName());
    }


    // TEST 4: deactivateInsurancePlan — happy path
    @Test
    void deactivateInsurancePlan_ShouldSetIsActiveFalse() {

        InsurancePlan plan = new InsurancePlan();
        plan.setId(1L);
        plan.setPlanName("Gold Plan");
        plan.setIsActive(true);

        when(insurancePlanRepository.findById(1L))
                .thenReturn(Optional.of(plan));

        when(insurancePlanRepository.save(any())).thenReturn(plan);

        // ACT
        insurancePlanService.deactivateInsurancePlan(1L);

        // ASSERT — plan should now be inactive
        assertFalse(plan.getIsActive());
        verify(insurancePlanRepository, times(1)).save(plan);
    }


    // TEST 5: deactivateInsurancePlan — plan not found
    @Test
    void deactivateInsurancePlan_ShouldThrowException_WhenPlanNotFound() {

        when(insurancePlanRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                insurancePlanService.deactivateInsurancePlan(99L)
        );

        assertTrue(ex.getMessage().contains("not"));
    }

    // TEST 6: assignInsurance — employee already has active plan
    @Test
    void assignInsurance_ShouldThrowException_WhenEmployeeAlreadyHasActivePlan() {

        AssignInsuranceRequestDTO dto = new AssignInsuranceRequestDTO();
        dto.setEmployeeId(10L);
        dto.setPlanId(1L);
        dto.setExpiryDate(LocalDate.now().plusYears(1));

        Employee employee = new Employee();
        employee.setUserId(10L);

        InsurancePlan plan = new InsurancePlan();
        plan.setId(1L);
        plan.setIsActive(true);

        when(employeeRepository.findById(10L))
                .thenReturn(Optional.of(employee));

        when(insurancePlanRepository.findById(1L))
                .thenReturn(Optional.of(plan));

        // fake: employee ALREADY has active insurance
        when(employeeInsuranceRepository.existsByEmployee_UserIdAndStatus(
                10L, InsuranceStatus.ACTIVE))
                .thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                insurancePlanService.assignInsurance(dto, "admin01")
        );

        assertTrue(ex.getMessage().contains("already has an active"));
    }


    // TEST 7: getEmployeeInsurance — no active insurance found
    @Test
    void getEmployeeInsurance_ShouldThrowException_WhenNoActiveInsurance() {

        when(employeeInsuranceRepository
                .findByEmployee_UserIdAndStatus(5L, InsuranceStatus.ACTIVE))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                insurancePlanService.getEmployeeInsurance(5L)
        );

        assertTrue(ex.getMessage().contains("No active insurance"));
    }
}