package com.sofkau.payroll_service.service;

import com.sofkau.payroll_service.dto.PayrollResponse;
import com.sofkau.payroll_service.entity.LocalEmployee;
import com.sofkau.payroll_service.entity.Payroll;
import com.sofkau.payroll_service.exception.EmployeeNotFoundException;
import com.sofkau.payroll_service.exception.PayrollNotFoundException;
import com.sofkau.payroll_service.mapper.PayrollMapper;
import com.sofkau.payroll_service.repository.LocalEmployeeRepository;
import com.sofkau.payroll_service.repository.PayrollRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.*;

import java.io.IOException;

@Service
public class PayrollServiceImpl implements PayRollService {
    private final PayrollRepository payrollRepository;
    private final LocalEmployeeRepository localEmployeeRepository;
    private final PayrollCalculationService calculationService;
    private final PayrollPdfGeneratorService pdfGeneratorService;
    private final PayrollMapper payrollMapper;

    public PayrollServiceImpl(
            PayrollRepository payrollRepository,
            LocalEmployeeRepository localEmployeeRepository,
            PayrollCalculationService calculationService,
            PayrollPdfGeneratorService pdfGeneratorService,
            PayrollMapper payrollMapper) {
        this.payrollRepository = payrollRepository;
        this.localEmployeeRepository = localEmployeeRepository;
        this.calculationService = calculationService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.payrollMapper = payrollMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public PayrollResponse getPayrollByEmployeeId(Long employeeId) {
        return payrollRepository.findFirstByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .map(payrollMapper::toResponse)
                .orElse(null);
    }

    private Payroll findPayrollById(Long payrollId) {
        return payrollRepository.findById(payrollId)
                .orElseThrow(() -> new PayrollNotFoundException("Nómina no encontrada."));
    }

    private LocalEmployee findEmployeeById(Long employeeId) {
        return localEmployeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(
                        "Empleado con id " + employeeId + " no ha sido sincronizado localmente."));
    }

    @Transactional
    @Override
    public PayrollResponse confirmPayroll(Long payrollId) {
        Payroll payroll = findPayrollById(payrollId);
        payroll.setConfirmed(true);
        return payrollMapper.toResponse(payrollRepository.save(payroll));
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] generatePdf(Long payrollId) throws DocumentException, IOException {
        Payroll payroll = findPayrollById(payrollId);
        return pdfGeneratorService.generatePdf(payroll);
    }

    @Transactional(readOnly = true)
    @Override
    public PayrollResponse getPayrollById(Long id) {
        return payrollMapper.toResponse(findPayrollById(id));
    }

    @Transactional
    @Override
    public PayrollResponse calculatePayroll(Long employeeId) {
        LocalEmployee employee = findEmployeeById(employeeId);
        Payroll calculatedPayroll = calculationService.calculatePayroll(employee);
        Payroll savedPayroll = payrollRepository.save(calculatedPayroll);
        return payrollMapper.toResponse(savedPayroll);
    }
}
