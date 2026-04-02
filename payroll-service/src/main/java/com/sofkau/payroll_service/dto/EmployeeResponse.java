package com.sofkau.payroll_service.dto;

import com.sofkau.payroll_service.entity.ContractType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EmployeeResponse {
    private Long id;
    private String name;
    private ContractType contractType;
    private BigDecimal grossSalary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeResponse() {}

    public EmployeeResponse(Long id, String name, ContractType contractType, BigDecimal grossSalary) {
        this.id = id;
        this.name = name;
        this.contractType = contractType;
        this.grossSalary = grossSalary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
