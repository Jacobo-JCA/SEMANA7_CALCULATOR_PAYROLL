package com.sofkau.service;

import com.sofkau.dto.EmployeeRequest;
import com.sofkau.dto.EmployeeResponse;
import com.sofkau.dto.EmployeeUpdateRequest;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse create(EmployeeRequest request);
    List<EmployeeResponse> findAll();
    EmployeeResponse findById(Long id);
    EmployeeResponse update(Long id, EmployeeUpdateRequest request);
}
