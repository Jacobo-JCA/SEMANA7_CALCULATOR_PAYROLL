package com.sofkau.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofkau.dto.ContractResponse;
import com.sofkau.dto.EmployeeRequest;
import com.sofkau.dto.EmployeeResponse;
import com.sofkau.entity.ContractType;
import com.sofkau.exception.EmployeeNotFoundException;
import com.sofkau.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private EmployeeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_return201_when_validEmployee() throws Exception {
        // GIVEN
        EmployeeRequest request = new EmployeeRequest("Juan Perez", new BigDecimal("3000"), ContractType.FULL_TIME);
        ContractResponse activeContract = new ContractResponse(1L, ContractType.FULL_TIME, LocalDate.now(), null);
        EmployeeResponse response = new EmployeeResponse(1L, "Juan Perez", new BigDecimal("3000"), LocalDateTime.now(),
                activeContract);

        when(service.create(any(EmployeeRequest.class))).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Juan Perez"));
    }

    @Test
    void should_return400_when_invalidEmployee() throws Exception {
        // GIVEN
        EmployeeRequest request = new EmployeeRequest("", new BigDecimal("-100"), null); // Invalid

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return200_when_findAll() throws Exception {
        // GIVEN
        EmployeeResponse r1 = new EmployeeResponse(1L, "Juan", new BigDecimal("2000"), LocalDateTime.now(), null);
        when(service.findAll()).thenReturn(List.of(r1));

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Juan"));
    }

    @Test
    void should_return200_when_findByIdExists() throws Exception {
        // GIVEN
        EmployeeResponse response = new EmployeeResponse(1L, "Juan", new BigDecimal("2000"), LocalDateTime.now(), null);
        when(service.findById(1L)).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan"));
    }

    @Test
    void should_return404_when_findByIdDoesNotExist() throws Exception {
        // GIVEN
        when(service.findById(anyLong())).thenThrow(new EmployeeNotFoundException("Not found"));

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_return200_when_updatingEmployee() throws Exception {
        // GIVEN
        EmployeeResponse response = new EmployeeResponse(1L, "Juan", new BigDecimal("3500"), LocalDateTime.now(), null);
        when(service.update(anyLong(), any())).thenReturn(response);

        // WHEN & THEN
        mockMvc.perform(patch("/api/v1/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());
    }
}
