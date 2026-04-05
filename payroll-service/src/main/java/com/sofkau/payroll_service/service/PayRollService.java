package com.sofkau.payroll_service.service;

import com.lowagie.text.DocumentException;
import com.sofkau.payroll_service.dto.PayrollResponse;

import java.io.IOException;

public interface PayRollService {
    PayrollResponse calculatePayroll(Long employeeId);
    PayrollResponse confirmPayroll(Long payrollId);
    byte[] generatePdf(Long payrollId) throws DocumentException, IOException;
    PayrollResponse getPayrollById(Long id);
    PayrollResponse getPayrollByEmployeeId(Long employeeId);
}
