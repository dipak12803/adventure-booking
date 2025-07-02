package com.adventure.adventurebooking.service;

import com.adventure.adventurebooking.entity.Booking;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import org.springframework.core.io.ClassPathResource;


@Service
@RequiredArgsConstructor
public class PdfService {

    public ByteArrayInputStream generateBookingPdf(Booking booking) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLUE);
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);

        // Add Logo
        try {
            ClassPathResource logoResource = new ClassPathResource("static/images/logo.png");
            Image logo = Image.getInstance(logoResource.getInputStream().readAllBytes());
            logo.scaleAbsolute(80, 80);
            logo.setAlignment(Image.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            // Optionally log missing logo
        }

        // Title
        Paragraph title = new Paragraph("Booking Confirmation", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(10f);
        title.setSpacingAfter(20f);
        document.add(title);

        // User Details
        document.add(new Paragraph("Name: " + booking.getUser().getName(), valueFont));
        document.add(new Paragraph("Email: " + booking.getUser().getEmail(), valueFont));
        document.add(new Paragraph("Booking ID: #" + booking.getId(), valueFont));
        document.add(Chunk.NEWLINE);

        // Booking Info Table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{2, 5});
        table.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

        addTableRow(table, "Sport", booking.getSport().getName(), labelFont, valueFont);
        addTableRow(table, "Date", booking.getBookingDate().toString(), labelFont, valueFont);
        addTableRow(table, "Time Slot", booking.getSlotTime(), labelFont, valueFont);
        addTableRow(table, "No. of People", String.valueOf(booking.getNumberOfPeople()), labelFont, valueFont);
        addTableRow(table, "Total Price", "₹" + booking.getTotalPrice(), labelFont, valueFont);
        addTableRow(table, "Payment Method", "Pay at Site", labelFont, valueFont);
        addTableRow(table, "Status", booking.getStatus(), labelFont, valueFont);

        document.add(table);

//        // QR Code
//        String qrContent = "Booking ID: " + booking.getId();
//        BarcodeQRCode qrCode = new BarcodeQRCode(qrContent, 150, 150, null);
//        Image qrImage = qrCode.getImage();
//        qrImage.setAlignment(Image.ALIGN_CENTER);
//        qrImage.setSpacingBefore(25f);
//        document.add(qrImage);
//
//        Paragraph qrNote = new Paragraph("Show this QR at the activity site for verification.", valueFont);
//        qrNote.setAlignment(Element.ALIGN_CENTER);
//        qrNote.setSpacingBefore(8f);
//        document.add(qrNote);

        document.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableRow(PdfPTable table, String key, String value, Font keyFont, Font valFont) {
        PdfPCell cell1 = new PdfPCell(new Phrase(key, keyFont));
        PdfPCell cell2 = new PdfPCell(new Phrase(value, valFont));
        cell1.setPadding(8);
        cell2.setPadding(8);
        cell1.setBorderColor(BaseColor.LIGHT_GRAY);
        cell2.setBorderColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell1);
        table.addCell(cell2);
    }
}
