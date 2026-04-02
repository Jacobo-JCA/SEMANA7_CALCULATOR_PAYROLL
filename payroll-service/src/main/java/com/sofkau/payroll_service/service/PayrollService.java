package com.sofkau.payroll_service.service;

import com.lowagie.text.Rectangle;
import com.sofkau.payroll_service.dto.PayrollResponse;
import com.sofkau.payroll_service.entity.ContractType;
import com.sofkau.payroll_service.entity.LocalEmployee;
import com.sofkau.payroll_service.entity.Payroll;
import com.sofkau.payroll_service.exception.EmployeeNotFoundException;
import com.sofkau.payroll_service.exception.PayrollNotFoundException;
import com.sofkau.payroll_service.repository.LocalEmployeeRepository;
import com.sofkau.payroll_service.repository.PayrollRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PayrollService {
    private final PayrollRepository payrollRepository;
    private final LocalEmployeeRepository localEmployeeRepository;

    public PayrollService(PayrollRepository payrollRepository, LocalEmployeeRepository localEmployeeRepository) {
        this.payrollRepository = payrollRepository;
        this.localEmployeeRepository = localEmployeeRepository;
    }

    @Transactional(readOnly = true)
    public PayrollResponse getPayrollByEmployeeId(Long employeeId) {
        return payrollRepository.findFirstByEmployeeIdOrderByCreatedAtDesc(employeeId)
            .map(this::mapToResponse)
            .orElse(null);
    }

    @Transactional
    public PayrollResponse confirmPayroll(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
            .orElseThrow(() -> new PayrollNotFoundException("Nómina no encontrada."));
        payroll.setConfirmed(true);
        return mapToResponse(payrollRepository.save(payroll));
    }

    @Transactional(readOnly = true)
    public byte[] generatePdf(Long payrollId) throws DocumentException, IOException {
        Payroll payroll = payrollRepository.findById(payrollId)
            .orElseThrow(() -> new PayrollNotFoundException("Nómina no encontrada."));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);

        document.open();

        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.BLACK);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
        Font netFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(0, 77, 179));

        // Header
        Paragraph title = new Paragraph("PAYROLL VOUCHER", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Employee Info Section
        Paragraph empInfo = new Paragraph("Employee Details", subtitleFont);
        empInfo.setSpacingAfter(10);
        document.add(empInfo);

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20);

        addInfoRow(infoTable, "Full Name:", payroll.getEmployeeName(), normalFont, boldFont);
        addInfoRow(infoTable, "Employee ID:", "#" + payroll.getEmployeeId().toString(), normalFont, boldFont);
        addInfoRow(infoTable, "Contract Type:", payroll.getContractType().toString().replace("_", " "), normalFont, boldFont);
        addInfoRow(infoTable, "Date Generated:", payroll.getCreatedAt().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")), normalFont, boldFont);
        
        document.add(infoTable);

        // Financial Breakdown Section
        Paragraph breakdownLabel = new Paragraph("Calculation Breakdown", subtitleFont);
        breakdownLabel.setSpacingAfter(10);
        document.add(breakdownLabel);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        // Headers
        addHeaderCell(table, "Concept", subtitleFont);
        addHeaderCell(table, "Amount", subtitleFont);

        NumberFormat usdFormater = NumberFormat.getCurrencyInstance(Locale.US);

        addConceptRow(table, "Monthly Gross Salary", usdFormater.format(payroll.getGrossSalary()), normalFont);
        addConceptRow(table, "Health & Pension Deductions (" + payroll.getDeductionPercentage() + "%)", "- " + usdFormater.format(payroll.getDeductionAmount()), normalFont);
        addConceptRow(table, "Transport/Performance Bonus (" + payroll.getBonusPercentage() + "%)", "+ " + usdFormater.format(payroll.getBonusAmount()), normalFont);

        document.add(table);

        // Net Salary Box
        Paragraph spacer = new Paragraph("\n");
        document.add(spacer);

        PdfPTable netTable = new PdfPTable(1);
        netTable.setWidthPercentage(100);

        PdfPCell netCell = new PdfPCell(new Phrase("NET PAYABLE: " + usdFormater.format(payroll.getNetSalary()), netFont));
        netCell.setBackgroundColor(new Color(240, 245, 255));
        netCell.setPadding(20);
        netCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        netCell.setBorderColor(new Color(0, 77, 179));
        netTable.addCell(netCell);
        document.add(netTable);
        document.close();
        return baos.toByteArray();
    }

    private void addInfoRow(PdfPTable table, String label, String value, Font normal, Font bold) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, normal));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setPaddingBottom(5);
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(value, bold));
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setPaddingBottom(5);
        table.addCell(cellValue);
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addConceptRow(PdfPTable table, String concept, String amount, Font font) {
        PdfPCell c1 = new PdfPCell(new Phrase(concept, font));
        c1.setPadding(8);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase(amount, font));
        c2.setPadding(8);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(c2);
    }

    @Transactional(readOnly = true)
    public PayrollResponse getPayrollById(Long id) {
        return payrollRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow(() -> new PayrollNotFoundException("Nómina con id " + id + " no encontrada."));
    }

    private BigDecimal getDeductionPercentage(ContractType type) {
        if (type == ContractType.PROFESSIONAL_SERVICES) {
            return new BigDecimal("8.00");
        }
        return new BigDecimal("9.45"); // FULL_TIME and PART_TIME
    }

    private BigDecimal getBonusPercentage(ContractType type) {
        if (type == ContractType.PROFESSIONAL_SERVICES) {
            return new BigDecimal("0.00");
        }
        return new BigDecimal("8.33"); // FULL_TIME and PART_TIME
    }

    @Transactional
    public PayrollResponse calculatePayroll(Long employeeId) {
        LocalEmployee employee = localEmployeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Empleado con id " + employeeId + " no ha sido sincronizado localmente."));

        BigDecimal grossSalary = employee.getGrossSalary();
        BigDecimal deductionPct = getDeductionPercentage(employee.getContractType());
        BigDecimal bonusPct = getBonusPercentage(employee.getContractType());

        BigDecimal deductionAmount = grossSalary.multiply(deductionPct).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal bonusAmount = grossSalary.multiply(bonusPct).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal netSalary = grossSalary.subtract(deductionAmount).add(bonusAmount).setScale(2, RoundingMode.HALF_UP);

        Payroll payroll = new Payroll();
        payroll.setEmployeeId(employee.getId());
        payroll.setEmployeeName(employee.getName());
        payroll.setContractType(employee.getContractType());
        payroll.setGrossSalary(grossSalary);
        payroll.setDeductionPercentage(deductionPct);
        payroll.setDeductionAmount(deductionAmount);
        payroll.setBonusPercentage(bonusPct);
        payroll.setBonusAmount(bonusAmount);
        payroll.setNetSalary(netSalary);
        payroll.setConfirmed(false);

        Payroll saved = payrollRepository.save(payroll);
        return mapToResponse(saved);
    }

    private PayrollResponse mapToResponse(Payroll entity) {
        return new PayrollResponse(
            entity.getId(),
            entity.getEmployeeId(),
            entity.getEmployeeName(),
            entity.getContractType(),
            entity.getGrossSalary(),
            entity.getDeductionPercentage(),
            entity.getDeductionAmount(),
            entity.getBonusPercentage(),
            entity.getBonusAmount(),
            entity.getNetSalary(),
            entity.getConfirmed(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
