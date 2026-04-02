package com.sofkau.payroll_service.controller;

import com.lowagie.text.DocumentException;
import com.sofkau.payroll_service.dto.PayrollResponse;
import com.sofkau.payroll_service.service.PayrollService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/payroll")
public class PayrollController {
    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    @PostMapping("/calculate/{employeeId}")
    public ResponseEntity<PayrollResponse> calculatePayroll(@PathVariable Long employeeId) {
        PayrollResponse response = payrollService.calculatePayroll(employeeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{payrollId}")
    public ResponseEntity<PayrollResponse> getPayrollById(@PathVariable Long payrollId) {
        return ResponseEntity.ok(payrollService.getPayrollById(payrollId));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<PayrollResponse> getPayrollByEmployeeId(@PathVariable Long employeeId) {
        PayrollResponse response = payrollService.getPayrollByEmployeeId(employeeId);
        if (response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{payrollId}/confirm")
    public ResponseEntity<PayrollResponse> confirmPayroll(@PathVariable Long payrollId) {
        return ResponseEntity.ok(payrollService.confirmPayroll(payrollId));
    }

    @GetMapping("/{payrollId}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long payrollId) throws DocumentException, IOException {
        byte[] pdfContent = payrollService.generatePdf(payrollId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "payroll-voucher-" + payrollId + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
}
