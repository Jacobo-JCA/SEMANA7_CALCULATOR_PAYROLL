package com.sofkau.service.impl;

import com.sofkau.dto.*;
import com.sofkau.entity.Contract;
import com.sofkau.entity.Employee;
import com.sofkau.exception.*;
import com.sofkau.infrastructure.messaging.EmployeeEventProducer;
import com.sofkau.mapper.EmployeeMapper;
import com.sofkau.repository.EmployeeRepository;
import com.sofkau.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeEventProducer eventProducer;
    private final EmployeeMapper mapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                           EmployeeEventProducer eventProducer,
                           EmployeeMapper mapper) {
        this.employeeRepository = employeeRepository;
        this.eventProducer = eventProducer;
        this.mapper = mapper;
    }

    private void validateNoDuplicate(String name) {
        if (employeeRepository.existsByName(name)) {
            throw new EmployeeAlreadyExistsException(
                    "El empleado con nombre " + name + " ya existe."
            );
        }
    }

    private Employee buildEmployee(EmployeeRequest request) {
        Employee employee = new Employee(request.name(), request.grossSalary());
        Contract contract = new Contract(request.contractType(), LocalDate.now(), employee);
        employee.addContract(contract);
        return employee;
    }

    private void applyUpdates(Employee employee, EmployeeUpdateRequest request) {
        if (request.name() != null) employee.setName(request.name());
        if (request.grossSalary() != null) employee.setGrossSalary(request.grossSalary());
    }

    private void publishCreatedEvent(Employee saved) {
        eventProducer.publishEmployeeCreated(new EmployeeCreatedEvent(
                saved.getId(),
                saved.getName(),
                saved.getContracts().get(0).getContractType(),
                saved.getGrossSalary(),
                LocalDateTime.now()
        ));
    }

    private void publishUpdatedEvent(Employee updated) {
        Contract active = employeeRepository
                .findActiveContractByEmployeeId(updated.getId())
                .orElseThrow(() -> new ActiveContractNotFoundException(
                        "El empleado con ID " + updated.getId() + " no tiene contrato activo."
                ));
        eventProducer.publishEmployeeUpdated(new EmployeeUpdatedEvent(
                updated.getId(),
                updated.getName(),
                active.getContractType(),
                updated.getGrossSalary(),
                LocalDateTime.now()
        ));
    }

    @Transactional
    @Override
    public EmployeeResponse create(EmployeeRequest request) {
        validateNoDuplicate(request.name());
        Employee employee = buildEmployee(request);
        Employee saved = employeeRepository.save(employee);
        publishCreatedEvent(saved);
        return mapper.toResponse(saved);
    }

    @Override
    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public EmployeeResponse findById(Long id) {
        return mapper.toResponse(findEmployeeById(id));
    }

    @Transactional
    @Override
    public EmployeeResponse update(Long id, EmployeeUpdateRequest request) {
        Employee employee = findEmployeeById(id);
        applyUpdates(employee, request);
        Employee updated = employeeRepository.save(employee);
        publishUpdatedEvent(updated);
        return mapper.toResponse(updated);
    }

    Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "Empleado con ID " + id + " no encontrado."
                ));
    }
}
