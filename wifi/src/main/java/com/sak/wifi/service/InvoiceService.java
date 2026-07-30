package com.sak.wifi.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sak.wifi.entity.Bill;
import com.sak.wifi.exception.ResourceNotFoundException;
import com.sak.wifi.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final BillRepository billRepository;

    public byte[] generateInvoicePdf(Long billId){
        Bill bill= billRepository.findById(billId)
                .orElseThrow(()->new ResourceNotFoundException("Bill not found"));

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        try{
            Document document=new Document();
            PdfWriter.getInstance(document,outputStream);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Paragraph title = new Paragraph("ISP BILL INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            table.addCell("Company");
            table.addCell(bill.getCustomer()
                    .getCompany()
                    .getCompanyName());

            table.addCell("Invoice Number");
            table.addCell(String.valueOf(bill.getId()));

            table.addCell("Customer");
            table.addCell(bill.getCustomer().getName());

            table.addCell("Address");
            table.addCell(bill.getCustomer().getAddress());

            table.addCell("Plan");
            table.addCell(bill.getPlan().getPlanName());

            table.addCell("Speed");
            table.addCell(bill.getPlan().getSpeedMbps() + " Mbps");

            table.addCell("Billing Month");
            table.addCell(bill.getBillingMonth());

            table.addCell("Amount");
            table.addCell("₹ " + bill.getAmount());

            table.addCell("Due Date");
            table.addCell(bill.getDueDate().toString());

            table.addCell("Status");
            table.addCell(bill.getPaymentStatus().name());

            document.add(table);
            document.close();

        }catch (Exception e){
            throw new RuntimeException("Error generating invoice pdf,e");
        }

        return outputStream.toByteArray();
    }
}
