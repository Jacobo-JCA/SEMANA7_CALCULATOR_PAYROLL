package com.sofkau.payroll_service.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payrolls")
@EntityListeners(AuditingEntityListener.class)
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false)
    private ContractType contractType;

    @Column(name = "gross_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal grossSalary;

    @Column(name = "deduction_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal deductionPercentage;

    @Column(name = "deduction_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal deductionAmount;

    @Column(name = "bonus_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal bonusPercentage;

    @Column(name = "bonus_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal bonusAmount;

    @Column(name = "net_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal netSalary;

    @Column(nullable = false)
    private Boolean confirmed = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Payroll() {}

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
