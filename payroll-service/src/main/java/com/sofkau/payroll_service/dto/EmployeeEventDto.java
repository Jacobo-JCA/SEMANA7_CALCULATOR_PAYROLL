package com.sofkau.payroll_service.dto;

import com.sofkau.payroll_service.entity.ContractType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmployeeEventDto(
        Long employeeId,
        String name,
        ContractType contractType,
        BigDecimal grossSalary,
        LocalDateTime timestamp
) {
}
