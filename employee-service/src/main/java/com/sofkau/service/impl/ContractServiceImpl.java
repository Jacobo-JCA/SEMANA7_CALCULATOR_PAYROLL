package com.sofkau.service.impl;

import com.sofkau.dto.ContractEndDateRequest;
import com.sofkau.dto.ContractResponse;
import com.sofkau.dto.ContractUpdateRequest;
import com.sofkau.entity.Contract;
import com.sofkau.exception.ContractNotBelongToEmployeeException;
import com.sofkau.exception.ContractNotFoundException;
import com.sofkau.exception.InvalidContractDateException;
import com.sofkau.mapper.EmployeeMapper;
import com.sofkau.repository.ContractRepository;
import com.sofkau.service.ContractService;
import com.sofkau.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class ContractServiceImpl implements ContractService {
    private final ContractRepository contractRepository;
    private final EmployeeMapper mapper;

    public ContractServiceImpl(ContractRepository contractRepository,
                           EmployeeService employeeService,
                           EmployeeMapper mapper) {
        this.contractRepository = contractRepository;
        this.mapper = mapper;
    }

    private Contract findContractById(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new ContractNotFoundException(
                        "Contrato con ID " + contractId + " no encontrado."
                ));
    }

    private void validateEndDate(LocalDate endDate) {
        if (endDate.isBefore(LocalDate.now())) {
            throw new InvalidContractDateException(
                    "La fecha de cierre no puede ser en el pasado."
            );
        }
    }

    private void validateContractBelongsToEmployee(Contract contract, Long employeeId) {
        if (!contract.getEmployee().getId().equals(employeeId)) {
            throw new ContractNotBelongToEmployeeException(
                    "El contrato ID " + contract.getId() +
                            " no pertenece al empleado con ID " + employeeId + "."
            );
        }
    }

    private void applyContractUpdates(Contract contract, ContractUpdateRequest request) {
        if (request.contractType() != null) contract.setContractType(request.contractType());
        if (request.startDate() != null) contract.setStartDate(request.startDate());
    }

    @Transactional
    @Override
    public void closeContract(Long employeeId, Long contractId, ContractEndDateRequest request) {
        validateEndDate(request.endDate());
        Contract contract = findContractById(contractId);
        validateContractBelongsToEmployee(contract, employeeId);
        contract.setEndDate(request.endDate());
    }

    @Transactional
    @Override
    public ContractResponse updateContract(Long employeeId, Long contractId, ContractUpdateRequest request) {
        Contract contract = findContractById(contractId);
        validateContractBelongsToEmployee(contract, employeeId);
        applyContractUpdates(contract, request);
        return mapper.toContractResponse(contract);
    }
}
