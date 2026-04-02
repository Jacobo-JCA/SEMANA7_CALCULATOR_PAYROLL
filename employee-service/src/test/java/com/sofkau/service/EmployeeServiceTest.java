package com.sofkau.service;

import com.sofkau.dto.*;
import com.sofkau.entity.Contract;
import com.sofkau.entity.ContractType;
import com.sofkau.entity.Employee;
import com.sofkau.exception.EmployeeAlreadyExistsException;
import com.sofkau.infrastructure.messaging.EmployeeEventProducer;
import com.sofkau.repository.ContractRepository;
import com.sofkau.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private EmployeeEventProducer eventProducer;

    @InjectMocks
    private EmployeeService service;

    @InjectMocks
    private ContractService contractService;

    @Test
    void should_createEmployee_when_validRequest() {
        EmployeeRequest request = new EmployeeRequest("Juan Perez", new BigDecimal("3000"), ContractType.FULL_TIME);
        Employee entity = new Employee("Juan Perez", new BigDecimal("3000"));
        entity.setId(1L);
        entity.setCreatedAt(LocalDateTime.now());
        
        Contract contract = new Contract(ContractType.FULL_TIME, LocalDate.now(), entity);
        entity.addContract(contract);

        when(employeeRepository.existsByName(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(entity);

        EmployeeResponse result = service.create(request);

        assertThat(result.name()).isEqualTo("Juan Perez");
        assertThat(result.grossSalary()).isEqualTo(new BigDecimal("3000"));
        assertThat(result.activeContract()).isNotNull();
        verify(employeeRepository).save(any(Employee.class));
        verify(eventProducer).publishEmployeeCreated(any(EmployeeCreatedEvent.class));
    }

    @Test
    void should_throwException_when_creatingDuplicateEmployee() {
        EmployeeRequest request = new EmployeeRequest("Juan Perez", new BigDecimal("3000"), ContractType.FULL_TIME);
        when(employeeRepository.existsByName("Juan Perez")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(EmployeeAlreadyExistsException.class);
    }

    @Test
    void should_updateEmployee_when_employeeExists() {
        Long id = 1L;
        EmployeeUpdateRequest request = new EmployeeUpdateRequest("Juan Actualizado", new BigDecimal("3500"));
        Employee existingEntity = new Employee("Juan Perez", new BigDecimal("3000"));
        existingEntity.setId(id);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(employeeRepository.save(any(Employee.class))).thenReturn(existingEntity);

        EmployeeResponse result = service.update(id, request);

        assertThat(result.name()).isEqualTo("Juan Actualizado");
        assertThat(result.grossSalary()).isEqualTo(new BigDecimal("3500"));
        verify(employeeRepository).findById(id);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void should_returnEmployee_when_findingByIdExists() {
        Long id = 1L;
        Employee entity = new Employee("Juan Perez", new BigDecimal("3000"));
        entity.setId(id);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(entity));

        EmployeeResponse result = service.findById(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("Juan Perez");
    }

    @Test
    void should_closeContract_when_validRequest() {
        Long empId = 1L;
        Long contId = 2L;
        ContractEndDateRequest request = new ContractEndDateRequest(LocalDate.now().plusDays(1));
        
        Employee employee = new Employee();
        employee.setId(empId);
        
        Contract contract = new Contract();
        contract.setId(contId);
        contract.setEmployee(employee);

        when(contractRepository.findById(contId)).thenReturn(Optional.of(contract));
        contractService.closeContract(empId, contId, request);

        assertThat(contract.getEndDate()).isEqualTo(request.endDate());
        verify(contractRepository).save(contract);
    }
}
