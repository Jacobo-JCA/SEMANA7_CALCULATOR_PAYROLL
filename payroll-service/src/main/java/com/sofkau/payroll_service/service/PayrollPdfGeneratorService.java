package com.sofkau.payroll_service.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sofkau.payroll_service.entity.Payroll;
import com.sofkau.payroll_service.util.PdfStyleHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PayrollPdfGeneratorService {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    private static final NumberFormat USD_FORMATTER =
            NumberFormat.getCurrencyInstance(Locale.US);

    public byte[] generatePdf(Payroll payroll) throws DocumentException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);

        document.open();
        addHeader(document);
        addEmployeeInfo(document, payroll);
        addFinancialBreakdown(document, payroll);
        addNetSalary(document, payroll);
        document.close();
        return baos.toByteArray();
    }

    private void addHeader(Document document) throws DocumentException {
        Paragraph title = new Paragraph("PAYROLL VOUCHER", PdfStyleHelper.TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
    }

    private void addEmployeeInfo(Document document, Payroll payroll) throws DocumentException {
        Paragraph empInfo = new Paragraph("Employee Details", PdfStyleHelper.SUBTITLE_FONT);
        empInfo.setSpacingAfter(10);
        document.add(empInfo);

        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20);

        addInfoRow(infoTable, "Full Name:", payroll.getEmployeeName());
        addInfoRow(infoTable, "Employee ID:", "#" + payroll.getEmployeeId());
        addInfoRow(infoTable, "Contract Type:",
                payroll.getContractType().toString().replace("_", " "));
        addInfoRow(infoTable, "Date Generated:",
                payroll.getCreatedAt().format(DATE_FORMATTER));

        document.add(infoTable);
    }

    private void addFinancialBreakdown(Document document, Payroll payroll) throws DocumentException {
        Paragraph breakdownLabel = new Paragraph("Calculation Breakdown",
                PdfStyleHelper.SUBTITLE_FONT);
        breakdownLabel.setSpacingAfter(10);
        document.add(breakdownLabel);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        addHeaderCell(table, "Concept");
        addHeaderCell(table, "Amount");

        addConceptRow(table, "Monthly Gross Salary",
                USD_FORMATTER.format(payroll.getGrossSalary()));
        addConceptRow(table,
                "Health & Pension Deductions (" + payroll.getDeductionPercentage() + "%)",
                "- " + USD_FORMATTER.format(payroll.getDeductionAmount()));
        addConceptRow(table,
                "Transport/Performance Bonus (" + payroll.getBonusPercentage() + "%)",
                "+ " + USD_FORMATTER.format(payroll.getBonusAmount()));

        document.add(table);
    }

    private void addNetSalary(Document document, Payroll payroll) throws DocumentException {
        document.add(new Paragraph("\n"));

        PdfPTable netTable = new PdfPTable(1);
        netTable.setWidthPercentage(100);

        PdfPCell netCell = new PdfPCell(
                new Phrase("NET PAYABLE: " + USD_FORMATTER.format(payroll.getNetSalary()),
                        PdfStyleHelper.NET_FONT));
        netCell.setBackgroundColor(PdfStyleHelper.NET_BG_COLOR);
        netCell.setPadding(20);
        netCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        netCell.setBorderColor(PdfStyleHelper.NET_BORDER_COLOR);

        netTable.addCell(netCell);
        document.add(netTable);
    }

    private void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, PdfStyleHelper.NORMAL_FONT));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setPaddingBottom(5);
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(value, PdfStyleHelper.BOLD_FONT));
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setPaddingBottom(5);
        table.addCell(cellValue);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, PdfStyleHelper.SUBTITLE_FONT));
        cell.setBackgroundColor(PdfStyleHelper.HEADER_BG_COLOR);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addConceptRow(PdfPTable table, String concept, String amount) {
        PdfPCell c1 = new PdfPCell(new Phrase(concept, PdfStyleHelper.NORMAL_FONT));
        c1.setPadding(8);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase(amount, PdfStyleHelper.NORMAL_FONT));
        c2.setPadding(8);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(c2);
    }
}
