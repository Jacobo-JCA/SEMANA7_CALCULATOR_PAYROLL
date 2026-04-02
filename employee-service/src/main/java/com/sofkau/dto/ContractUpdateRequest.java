package com.sofkau.dto;

import com.sofkau.entity.ContractType;
import java.time.LocalDate;

public record ContractUpdateRequest(
    ContractType contractType,
    LocalDate startDate
) {}
