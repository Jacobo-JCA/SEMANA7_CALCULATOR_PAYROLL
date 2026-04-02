package com.sofkau.payroll_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofkau.payroll_service.dto.PayrollResponse;
import com.sofkau.payroll_service.entity.ContractType;
import com.sofkau.payroll_service.service.PayrollService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PayrollController.class)
class PayrollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PayrollService payrollService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_return201_when_calculatingPayroll() throws Exception {
        // GIVEN
        Long employeeId = 1L;
        PayrollResponse response = new PayrollResponse(100L, employeeId, "Juan", ContractType.FULL_TIME, BigDecimal.valueOf(3000), BigDecimal.valueOf(10), BigDecimal.valueOf(300), BigDecimal.valueOf(8), BigDecimal.valueOf(240), BigDecimal.valueOf(2940), false, LocalDateTime.now(), null);
        when(payrollService.calculatePayroll(employeeId)).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/payroll/calculate/1"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(100L));
    }

    @Test
    void should_return200_when_gettingPayrollById() throws Exception {
        // GIVEN
        PayrollResponse response = new PayrollResponse(1L, 2L, "Juan", ContractType.FULL_TIME, null, null, null, null, null, null, false, null, null);
        when(payrollService.getPayrollById(1L)).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/payroll/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.employeeName").value("Juan"));
    }

    @Test
    void should_return200_when_gettingPayrollByEmployeeIdExists() throws Exception {
        // GIVEN
        PayrollResponse response = new PayrollResponse(1L, 2L, "Juan", null, null, null, null, null, null, null, false, null, null);
        when(payrollService.getPayrollByEmployeeId(2L)).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/payroll/employee/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void should_return404_when_gettingPayrollByEmployeeIdDoesNotExist() throws Exception {
        // GIVEN
        when(payrollService.getPayrollByEmployeeId(anyLong())).thenReturn(null);

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/payroll/employee/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void should_return200_when_confirmingPayroll() throws Exception {
        // GIVEN
        PayrollResponse response = new PayrollResponse(1L, null, null, null, null, null, null, null, null, null, true, null, null);
        when(payrollService.confirmPayroll(1L)).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(patch("/api/v1/payroll/1/confirm"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.confirmed").value(true));
    }

    @Test
    void should_return200_when_downloadingPdf() throws Exception {
        // GIVEN
        byte[] pdfContent = new byte[]{1, 2, 3};
        when(payrollService.generatePdf(1L)).thenReturn(pdfContent);

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/payroll/1/pdf"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "application/pdf"));
    }
}
