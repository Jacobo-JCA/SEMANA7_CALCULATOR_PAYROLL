package com.sofkau.dto;

import com.sofkau.entity.ContractType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmployeeCreatedEvent(
        Long employeeId,
        String name,
        ContractType contractType,
        BigDecimal grossSalary,
        LocalDateTime timestamp
) {
}
