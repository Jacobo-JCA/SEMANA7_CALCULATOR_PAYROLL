package com.sofkau.dto;

import java.math.BigDecimal;

public record EmployeeUpdateRequest(
    String name,
    BigDecimal grossSalary
) {}
