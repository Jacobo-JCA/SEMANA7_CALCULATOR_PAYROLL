package com.sofkau.payroll_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "local_employees")
public class LocalEmployee {

    @Id
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private ContractType contractType;
    private BigDecimal grossSalary;

    public LocalEmployee() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ContractType getContractType() { return contractType; }
    public void setContractType(ContractType contractType) { this.contractType = contractType; }
    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }
}
