package com.sofkau.controller;

import com.sofkau.config.RoleInterceptor.AdminOnly;
import com.sofkau.dto.*;
import com.sofkau.service.ContractService;
import com.sofkau.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final ContractService contractService;

    public EmployeeController(EmployeeService employeeService, ContractService contractService) {
        this.employeeService = employeeService;
        this.contractService = contractService;
    }

    @AdminOnly
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> findAll() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @AdminOnly
    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(@PathVariable Long id, @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @AdminOnly
    @PatchMapping("/{id}/contracts/{contractId}")
    public ResponseEntity<ContractResponse> updateContract(
            @PathVariable Long id,
            @PathVariable Long contractId,
            @Valid @RequestBody ContractUpdateRequest request) {
        return ResponseEntity.ok(contractService.updateContract(id, contractId, request));
    }

    @AdminOnly
    @PatchMapping("/{id}/contracts/{contractId}/end-date")
    public ResponseEntity<Void> closeContract(
            @PathVariable Long id,
            @PathVariable Long contractId,
            @Valid @RequestBody ContractEndDateRequest request) {
        contractService.closeContract(id, contractId, request);
        return ResponseEntity.noContent().build();
    }
}