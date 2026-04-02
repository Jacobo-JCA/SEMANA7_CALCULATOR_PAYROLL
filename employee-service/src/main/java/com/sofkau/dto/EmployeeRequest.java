package com.sofkau.dto;

import com.sofkau.entity.ContractType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record EmployeeRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    String name,
    @NotNull(message = "El salario bruto es obligatorio")
    @Positive(message = "El salario debe ser positivo")
    BigDecimal grossSalary,
    @NotNull(message = "El tipo de contrato es obligatorio")
    ContractType contractType
) {}
