package com.adventure.adventurebooking.controller;

import com.adventure.adventurebooking.dto.BookingRequest;
import com.adventure.adventurebooking.entity.*;
import com.adventure.adventurebooking.repository.*;
import com.adventure.adventurebooking.service.UserService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final SportRepository sportRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final SlotRepository slotRepository;

    @GetMapping("/home")
    public String showHomePage(Model model) {
        model.addAttribute("sports", sportRepository.findAll());
        return "home";
    }

    @GetMapping("/sports")
    public String viewSports(Model model) {
        model.addAttribute("sports", sportRepository.findAll());
        return "sports";
    }

    @GetMapping("/book/{id}")
    public String showBookingForm(@PathVariable Long id,
                                  @RequestParam(value = "error", required = false) String error,
                                  Model model) {
        var sport = sportRepository.findById(id).orElse(null);
        if (sport == null) return "redirect:/sports";

        model.addAttribute("sport", sport);
        model.addAttribute("slots", slotRepository.findBySport(sport));
        model.addAttribute("today", java.time.LocalDate.now());

        if ("slots".equals(error)) {
            model.addAttribute("slotError", "Not enough slots available for your booking.");
        }

        return "book";
    }

    @PostMapping("/book")
    public String confirmBooking(@ModelAttribute BookingRequest request,
                                 @AuthenticationPrincipal org.springframework.security.core.userdetails.User springUser,
                                 Model model) {
        var user = userService.getUserByEmail(springUser.getUsername());
        var sport = sportRepository.findById(request.getSportId()).orElse(null);

        if (sport == null) return "redirect:/sports";
        if (sport.getAvailableSlots() < request.getNumberOfPeople()) {
            return "redirect:/book/" + request.getSportId() + "?error=slots";
        }

        sport.setAvailableSlots(sport.getAvailableSlots() - request.getNumberOfPeople());
        sportRepository.save(sport);

        var booking = Booking.builder()
                .user(user)
                .sport(sport)
                .bookingDate(request.getBookingDate())
                .slotTime(request.getSlotTime())
                .numberOfPeople(request.getNumberOfPeople())
                .totalPrice(request.getNumberOfPeople() * sport.getPricePerPerson())
                .status("CONFIRMED")
                .build();

        bookingRepository.save(booking);
        model.addAttribute("booking", booking);
        return "success";
    }

    @GetMapping("/my-bookings")
    public String viewBookings(Model model,
                               @AuthenticationPrincipal org.springframework.security.core.userdetails.User springUser) {
        var user = userService.getUserByEmail(springUser.getUsername());
        model.addAttribute("bookings", bookingRepository.findByUser(user));
        return "bookings";
    }

    @PostMapping("/cancel-booking/{id}")
    public String cancelBooking(@PathVariable Long id,
                                @AuthenticationPrincipal org.springframework.security.core.userdetails.User springUser) {
        var booking = bookingRepository.findById(id).orElse(null);
        if (booking != null && booking.getUser().getEmail().equals(springUser.getUsername())) {
            var sport = booking.getSport();
            sport.setAvailableSlots(sport.getAvailableSlots() + booking.getNumberOfPeople());
            sportRepository.save(sport);
            bookingRepository.delete(booking);
        }
        return "redirect:/my-bookings";
    }

    // ✅ PDF Download Endpoint
    @GetMapping("/download-booking/{id}")
    public void downloadBookingPDF(@PathVariable Long id,
                                   HttpServletResponse response,
                                   @AuthenticationPrincipal org.springframework.security.core.userdetails.User springUser) throws IOException {
        var booking = bookingRepository.findById(id).orElse(null);
        if (booking == null || !booking.getUser().getEmail().equals(springUser.getUsername())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=PlanAdventure-Booking.pdf");

        var document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(0, 102, 204));
        Font labelFont = new Font(Font.HELVETICA, 13, Font.BOLD, Color.BLACK);
        Font normalFont = new Font(Font.HELVETICA, 13);

        // Add Logo
        try {
            var logoResource = new ClassPathResource("static/images/logo.png");
            var logo = Image.getInstance(logoResource.getInputStream().readAllBytes());
            logo.scaleToFit(100, 100);
            logo.setAlignment(Image.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            // Optional: handle if logo fails
        }

        document.add(new Paragraph("PlanAdventure - Booking Confirmation", titleFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Hello, " + booking.getUser().getName(), normalFont));
        document.add(new Paragraph("Thank you for booking with us!", normalFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Booking Details:", labelFont));
        document.add(new Paragraph("Sport: " + booking.getSport().getName(), normalFont));
        document.add(new Paragraph("Date: " + booking.getBookingDate(), normalFont));
        document.add(new Paragraph("Time Slot: " + booking.getSlotTime(), normalFont));
        document.add(new Paragraph("Number of People: " + booking.getNumberOfPeople(), normalFont));
        document.add(new Paragraph("Total Price: ₹" + booking.getTotalPrice(), normalFont));
        document.add(new Paragraph("Status: " + booking.getStatus(), normalFont));
        document.add(new Paragraph("Payment Mode: Pay at Site", normalFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Contact our team on arrival and carry this PDF as your ticket.", normalFont));
        document.add(new Paragraph(" "));

//        // QR Code Placeholder (you can replace this with actual generated QR code image)
//        try {
//            var qrImage = Image.getInstance("https://api.qrserver.com/v1/create-qr-code/?size=130x130&data=BookingID:" + booking.getId());
//            qrImage.setAlignment(Image.ALIGN_CENTER);
//            qrImage.scaleToFit(120, 120);
//            document.add(qrImage);
//        } catch (Exception e) {
//            // QR fallback
//        }

        document.add(new Paragraph("Booking ID: #" + booking.getId(), new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY)));

        document.close();
    }
}
