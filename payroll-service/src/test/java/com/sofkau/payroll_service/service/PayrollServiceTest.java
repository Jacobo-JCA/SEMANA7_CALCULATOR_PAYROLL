package com.sofkau.payroll_service.service;

import com.sofkau.payroll_service.dto.PayrollResponse;
import com.sofkau.payroll_service.entity.ContractType;
import com.sofkau.payroll_service.entity.LocalEmployee;
import com.sofkau.payroll_service.entity.Payroll;
import com.sofkau.payroll_service.exception.EmployeeNotFoundException;
import com.sofkau.payroll_service.exception.PayrollNotFoundException;
import com.sofkau.payroll_service.repository.LocalEmployeeRepository;
import com.sofkau.payroll_service.repository.PayrollRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private LocalEmployeeRepository employeeRepository;

    @InjectMocks
    private PayrollService service;

    @Test
    void should_calculatePayroll_when_employeeExistsFullTime() {
        // GIVEN
        Long employeeId = 1L;
        LocalEmployee employee = new LocalEmployee();
        employee.setId(employeeId);
        employee.setName("Juan");
        employee.setContractType(ContractType.FULL_TIME);
        employee.setGrossSalary(new BigDecimal("3000"));

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(payrollRepository.save(any(Payroll.class))).thenAnswer(i -> i.getArgument(0));

        // WHEN
        PayrollResponse result = service.calculatePayroll(employeeId);

        // THEN
        assertThat(result.getGrossSalary()).isEqualByComparingTo("3000");
        // For FULL_TIME: 10% (wait, actual code has 9.45% and 8.33% from view_file earlier)
        // Let's check code logic: 
        // 9.45% deduction, 8.33% bonus
        // 3000 * 0.0945 = 283.5
        // 3000 * 0.0833 = 249.9
        // net = 3000 - 283.5 + 249.9 = 2966.4
        assertThat(result.getDeductionAmount()).isEqualByComparingTo("283.50");
        assertThat(result.getBonusAmount()).isEqualByComparingTo("249.90");
        assertThat(result.getNetSalary()).isEqualByComparingTo("2966.40");
        verify(payrollRepository).save(any(Payroll.class));
    }

    @Test
    void should_throwException_when_employeeNotFound() {
        // GIVEN
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> service.calculatePayroll(employeeId))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("Empleado con id 1 no ha sido sincronizado localmente.");
    }

    @Test
    void should_confirmPayroll_when_payrollExists() {
        // GIVEN
        Long payrollId = 100L;
        Payroll payroll = new Payroll();
        payroll.setId(payrollId);
        payroll.setConfirmed(false);

        when(payrollRepository.findById(payrollId)).thenReturn(Optional.of(payroll));
        when(payrollRepository.save(any(Payroll.class))).thenReturn(payroll);

        // WHEN
        PayrollResponse result = service.confirmPayroll(payrollId);

        // THEN
        assertThat(result.getConfirmed()).isTrue();
        verify(payrollRepository).save(payroll);
    }

    @Test
    void should_throwException_when_confirmingNonExistentPayroll() {
        // GIVEN
        Long payrollId = 999L;
        when(payrollRepository.findById(payrollId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> service.confirmPayroll(payrollId))
                .isInstanceOf(PayrollNotFoundException.class);
    }

    @Test
    void should_returnPayroll_when_findingByIdExists() {
        // GIVEN
        Long id = 1L;
        Payroll payroll = new Payroll();
        payroll.setId(id);
        payroll.setEmployeeName("Juan");

        when(payrollRepository.findById(id)).thenReturn(Optional.of(payroll));

        // WHEN
        PayrollResponse result = service.getPayrollById(id);

        // THEN
        assertThat(result.getEmployeeName()).isEqualTo("Juan");
    }

    @Test
    void should_generatePdf_when_payrollExists() throws Exception {
        // GIVEN
        Long id = 1L;
        Payroll payroll = new Payroll();
        payroll.setId(id);
        payroll.setEmployeeId(2L);
        payroll.setEmployeeName("Juan");
        payroll.setContractType(ContractType.FULL_TIME);
        payroll.setGrossSalary(BigDecimal.valueOf(1000));
        payroll.setDeductionPercentage(BigDecimal.valueOf(9.45));
        payroll.setDeductionAmount(BigDecimal.valueOf(94.5));
        payroll.setBonusPercentage(BigDecimal.valueOf(8.33));
        payroll.setBonusAmount(BigDecimal.valueOf(83.3));
        payroll.setNetSalary(BigDecimal.valueOf(988.8));
        payroll.setCreatedAt(LocalDateTime.now());

        when(payrollRepository.findById(id)).thenReturn(Optional.of(payroll));

        // WHEN
        byte[] pdf = service.generatePdf(id);

        // THEN
        assertThat(pdf).isNotEmpty();
    }
}
