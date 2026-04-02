package com.sofkau.mapper;

import com.sofkau.dto.ContractResponse;
import com.sofkau.dto.EmployeeResponse;
import com.sofkau.entity.Contract;
import com.sofkau.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeResponse toResponse(Employee employee) {
        Contract activeContract = employee.getContracts().stream()
                .filter(c -> c.getEndDate() == null)
                .findFirst()
                .orElse(null);
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getGrossSalary(),
                employee.getCreatedAt(),
                activeContract != null ? toContractResponse(activeContract) : null
        );
    }

    public ContractResponse toContractResponse(Contract contract) {
        return new ContractResponse(
                contract.getId(),
                contract.getContractType(),
                contract.getStartDate(),
                contract.getEndDate()
        );
    }
}
