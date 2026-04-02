package com.sofkau.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmployeeResponse(
    Long id,
    String name,
    BigDecimal grossSalary,
    LocalDateTime createdAt,
    ContractResponse activeContract
) {}
