package com.sofkau.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ContractEndDateRequest(
    @NotNull(message = "La fecha de cierre es obligatoria")
    LocalDate endDate
) {}
