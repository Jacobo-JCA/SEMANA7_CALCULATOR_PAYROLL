package com.sofkau.payroll_service.mapper;

import com.sofkau.payroll_service.dto.PayrollResponse;
import com.sofkau.payroll_service.entity.Payroll;
import org.springframework.stereotype.Component;

@Component
public class PayrollMapper {
    public PayrollResponse toResponse(Payroll entity) {
        return new PayrollResponse(
                entity.getId(),
                entity.getEmployeeId(),
                entity.getEmployeeName(),
                entity.getContractType(),
                entity.getGrossSalary(),
                entity.getDeductionPercentage(),
                entity.getDeductionAmount(),
                entity.getBonusPercentage(),
                entity.getBonusAmount(),
                entity.getNetSalary(),
                entity.getConfirmed(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
