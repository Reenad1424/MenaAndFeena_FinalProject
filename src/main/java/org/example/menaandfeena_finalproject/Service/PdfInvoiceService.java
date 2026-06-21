package org.example.menaandfeena_finalproject.Service;


import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.DTO.Out.PaymentInvoiceDTO;
import org.springframework.stereotype.Service;
import com.lowagie.text.Font;
import java.awt.Color;
import com.lowagie.text.Element;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import com.lowagie.text.Image;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
@Service
@RequiredArgsConstructor
public class PdfInvoiceService {
    private final PaymentService paymentService;

    public byte[] generateInvoice(String paymentId) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PaymentInvoiceDTO invoice = paymentService.getPaymentInvoice(paymentId);

        Document document = new Document();

        PdfWriter.getInstance(document, out);

        document.open();
        String logoPath = "src/main/resources/static/images/logo.jpeg";

        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(120, 120);
        logo.setAlignment(Image.ALIGN_CENTER);

        document.add(logo);
        document.add(new Paragraph(" "));

        Font titleFont = new Font(
                Font.HELVETICA,
                18,
                Font.BOLD,
                new Color(34, 139, 34)
        );

        Paragraph title = new Paragraph(
                "Mena & Feena Event Payment Invoice",
                titleFont
        );

        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        Paragraph line = new Paragraph(
                "________________________________________"
        );

        line.setAlignment(Element.ALIGN_CENTER);

        line.getFont().setColor(new Color(34, 139, 34));

        document.add(line);
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        PdfPTable table = new PdfPTable(2);
        PdfPCell cell1 = new PdfPCell();
        PdfPCell cell2 = new PdfPCell();
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        table.addCell(createCell("Payment ID", true));
        table.addCell(createCell(invoice.getMoyasarPaymentId(), false));
        table.addCell(createCell("Event", true));
        table.addCell(createCell(invoice.getEventTitle(), false));

        table.addCell(createCell("Amount", true));
        table.addCell(createCell(invoice.getAmount(), false));

        table.addCell(createCell("Status", true));
        table.addCell(createCell("✓ " + invoice.getPaymentStatus().toUpperCase(), false));
        table.addCell(createCell("Card Company", true));
        table.addCell(createCell(invoice.getCardCompany(), false));

        table.addCell(createCell("Card Holder", true));
        table.addCell(createCell(invoice.getCardHolderName(), false));

        table.addCell(createCell("Date", true));
        table.addCell(createCell(invoice.getCreatedAt(), false));

        document.add(table);
        document.add(new Paragraph(" "));

        Paragraph thankYou = new Paragraph(
                "Thank you for using Mena & Feena",
                new Font(Font.HELVETICA, 12, Font.ITALIC)
        );

        thankYou.setAlignment(Element.ALIGN_CENTER);

        document.add(thankYou);
        document.close();
        return out.toByteArray();

    }


    private PdfPCell createCell(String text, boolean label) {
        Font font = new Font(Font.HELVETICA, 11, label ? Font.BOLD : Font.NORMAL);

        PdfPCell cell = new PdfPCell(new Paragraph(text, font));

        if (label) {
            cell.setBackgroundColor(new Color(220, 237, 200));
        } else {
            cell.setBackgroundColor(new Color(250, 246, 238));
        }

        cell.setPadding(8);
        cell.setBorderColor(new Color(243, 156, 18));

        return cell;
    }











}