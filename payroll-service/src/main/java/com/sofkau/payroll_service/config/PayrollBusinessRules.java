package com.sofkau.payroll_service.config;

import com.sofkau.payroll_service.entity.ContractType;

import java.math.BigDecimal;

public class PayrollBusinessRules {
    public static final BigDecimal PROFESSIONAL_SERVICES_DEDUCTION = new BigDecimal("8.00");
    public static final BigDecimal STANDARD_DEDUCTION = new BigDecimal("9.45");
    public static final BigDecimal NO_BONUS = new BigDecimal("0.00");
    public static final BigDecimal STANDARD_BONUS = new BigDecimal("8.33");
    public static final BigDecimal HUNDRED = new BigDecimal("100");
    public static final int DECIMAL_SCALE = 2;

    private PayrollBusinessRules() {
        throw new IllegalStateException("Utility class");
    }

    public static BigDecimal getDeductionPercentage(ContractType type) {
        return type == ContractType.PROFESSIONAL_SERVICES
                ? PROFESSIONAL_SERVICES_DEDUCTION
                : STANDARD_DEDUCTION;
    }

    public static BigDecimal getBonusPercentage(ContractType type) {
        return type == ContractType.PROFESSIONAL_SERVICES
                ? NO_BONUS
                : STANDARD_BONUS;
    }
}
