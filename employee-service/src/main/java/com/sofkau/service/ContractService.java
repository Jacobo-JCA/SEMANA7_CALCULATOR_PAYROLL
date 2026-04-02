package com.sofkau.service;

import com.sofkau.dto.ContractEndDateRequest;
import com.sofkau.dto.ContractResponse;
import com.sofkau.dto.ContractUpdateRequest;

public interface ContractService {
    void closeContract(Long employeeId, Long contractId, ContractEndDateRequest request);
    ContractResponse updateContract(Long employeeId, Long contractId, ContractUpdateRequest request);
}
