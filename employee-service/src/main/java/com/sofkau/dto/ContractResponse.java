package com.sofkau.dto;

import com.sofkau.entity.ContractType;
import java.time.LocalDate;

public record ContractResponse(
    Long id,
    ContractType contractType,
    LocalDate startDate,
    LocalDate endDate
) {}
