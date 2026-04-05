package com.sofkau.payroll_service.service;

import com.sofkau.payroll_service.config.PayrollBusinessRules;
import com.sofkau.payroll_service.entity.ContractType;
import com.sofkau.payroll_service.entity.LocalEmployee;
import com.sofkau.payroll_service.entity.Payroll;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PayrollCalculationService {
    public Payroll calculatePayroll(LocalEmployee employee) {
        BigDecimal grossSalary = employee.getGrossSalary();
        ContractType contractType = employee.getContractType();

        BigDecimal deductionPct = PayrollBusinessRules.getDeductionPercentage(contractType);
        BigDecimal bonusPct = PayrollBusinessRules.getBonusPercentage(contractType);

        BigDecimal deductionAmount = calculateAmount(grossSalary, deductionPct);
        BigDecimal bonusAmount = calculateAmount(grossSalary, bonusPct);
        BigDecimal netSalary = calculateNetSalary(grossSalary, deductionAmount, bonusAmount);

        return buildPayroll(employee, grossSalary, deductionPct, deductionAmount,
                bonusPct, bonusAmount, netSalary);
    }

    private BigDecimal calculateAmount(BigDecimal base, BigDecimal percentage) {
        return base.multiply(percentage)
                .divide(PayrollBusinessRules.HUNDRED,
                        PayrollBusinessRules.DECIMAL_SCALE,
                        RoundingMode.HALF_UP);
    }

    private BigDecimal calculateNetSalary(BigDecimal gross, BigDecimal deduction, BigDecimal bonus) {
        return gross.subtract(deduction)
                .add(bonus)
                .setScale(PayrollBusinessRules.DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    private Payroll buildPayroll(LocalEmployee employee, BigDecimal grossSalary,
                                 BigDecimal deductionPct, BigDecimal deductionAmount,
                                 BigDecimal bonusPct, BigDecimal bonusAmount,
                                 BigDecimal netSalary) {
        Payroll payroll = new Payroll();
        payroll.setEmployeeId(employee.getId());
        payroll.setEmployeeName(employee.getName());
        payroll.setContractType(employee.getContractType());
        payroll.setGrossSalary(grossSalary);
        payroll.setDeductionPercentage(deductionPct);
        payroll.setDeductionAmount(deductionAmount);
        payroll.setBonusPercentage(bonusPct);
        payroll.setBonusAmount(bonusAmount);
        payroll.setNetSalary(netSalary);
        payroll.setConfirmed(false);
        return payroll;
    }
}
