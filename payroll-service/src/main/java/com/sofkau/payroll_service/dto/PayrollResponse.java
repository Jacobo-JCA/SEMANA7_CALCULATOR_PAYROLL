package com.sofkau.payroll_service.dto;

import com.sofkau.payroll_service.entity.ContractType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PayrollResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private ContractType contractType;
    private BigDecimal grossSalary;
    private BigDecimal deductionPercentage;
    private BigDecimal deductionAmount;
    private BigDecimal bonusPercentage;
    private BigDecimal bonusAmount;
    private BigDecimal netSalary;
    private Boolean confirmed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PayrollResponse() {}

    public PayrollResponse(Long id, Long employeeId, String employeeName, ContractType contractType, BigDecimal grossSalary, BigDecimal deductionPercentage, BigDecimal deductionAmount, BigDecimal bonusPercentage, BigDecimal bonusAmount, BigDecimal netSalary, Boolean confirmed, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.contractType = contractType;
        this.grossSalary = grossSalary;
        this.deductionPercentage = deductionPercentage;
        this.deductionAmount = deductionAmount;
        this.bonusPercentage = bonusPercentage;
        this.bonusAmount = bonusAmount;
        this.netSalary = netSalary;
        this.confirmed = confirmed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public ContractType getContractType() { return contractType; }
    public void setContractType(ContractType contractType) { this.contractType = contractType; }
    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }
    public BigDecimal getDeductionPercentage() { return deductionPercentage; }
    public void setDeductionPercentage(BigDecimal deductionPercentage) { this.deductionPercentage = deductionPercentage; }
    public BigDecimal getDeductionAmount() { return deductionAmount; }
    public void setDeductionAmount(BigDecimal deductionAmount) { this.deductionAmount = deductionAmount; }
    public BigDecimal getBonusPercentage() { return bonusPercentage; }
    public void setBonusPercentage(BigDecimal bonusPercentage) { this.bonusPercentage = bonusPercentage; }
    public BigDecimal getBonusAmount() { return bonusAmount; }
    public void setBonusAmount(BigDecimal bonusAmount) { this.bonusAmount = bonusAmount; }
    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }
    public Boolean getConfirmed() { return confirmed; }
    public void setConfirmed(Boolean confirmed) { this.confirmed = confirmed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
