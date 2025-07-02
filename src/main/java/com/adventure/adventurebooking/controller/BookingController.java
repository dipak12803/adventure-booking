package com.adventure.adventurebooking.controller;

import ch.qos.logback.core.model.Model;
import com.adventure.adventurebooking.dto.BookingRequest;
import com.adventure.adventurebooking.entity.*;
import com.adventure.adventurebooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SportRepository sportRepository;

    @Autowired
    private UserRepository userRepository;

    // Book a sport
    @PostMapping
    public ResponseEntity<?> bookSport(@AuthenticationPrincipal User user,
                                       @RequestBody BookingRequest request) {

        Sport sport = sportRepository.findById(request.getSportId()).orElse(null);
        if (sport == null) return ResponseEntity.badRequest().body("Sport not found");

        if (sport.getAvailableSlots() < request.getNumberOfPeople()) {
            return ResponseEntity.badRequest().body("Not enough slots available");
        }

        // Decrease available slots
        sport.setAvailableSlots(sport.getAvailableSlots() - request.getNumberOfPeople());
        sportRepository.save(sport);

        double total = sport.getPricePerPerson() * request.getNumberOfPeople();

        Booking booking = Booking.builder()
                .user(user)
                .sport(sport)
                .bookingDate(request.getBookingDate())
                .slotTime(request.getSlotTime()) // ✅ NEW line
                .numberOfPeople(request.getNumberOfPeople())
                .totalPrice(total)
                .status("CONFIRMED")
                .build();

        return ResponseEntity.ok(bookingRepository.save(booking));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, @AuthenticationPrincipal User user) {
        return bookingRepository.findById(id)
                .filter(booking -> booking.getUser().getId().equals(user.getId()))
                .map(booking -> {
                    Sport sport = booking.getSport();
                    sport.setAvailableSlots(sport.getAvailableSlots() + booking.getNumberOfPeople());
                    sportRepository.save(sport);

                    bookingRepository.delete(booking);
                    return ResponseEntity.ok("Booking cancelled");
                })
                .orElse(ResponseEntity.status(403).body("Unauthorized or booking not found"));
    }


    // Get current user's bookings
    @GetMapping
    public ResponseEntity<List<Booking>> getUserBookings(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookingRepository.findByUser(user));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id,
                                           @RequestBody BookingRequest request,
                                           @AuthenticationPrincipal User user) {
        return bookingRepository.findById(id)
                .filter(booking -> booking.getUser().getId().equals(user.getId()))
                .map(booking -> {
                    Sport sport = booking.getSport();

                    // Restore previous slots
                    sport.setAvailableSlots(sport.getAvailableSlots() + booking.getNumberOfPeople());

                    // Check for enough slots for updated number
                    if (sport.getAvailableSlots() < request.getNumberOfPeople()) {
                        return ResponseEntity.badRequest().body("Not enough slots available");
                    }

                    // Deduct new slot count
                    sport.setAvailableSlots(sport.getAvailableSlots() - request.getNumberOfPeople());
                    sportRepository.save(sport);

                    booking.setNumberOfPeople(request.getNumberOfPeople());
                    booking.setBookingDate(request.getBookingDate());
                    booking.setSlotTime(request.getSlotTime()); // ✅ THIS LINE
                    booking.setTotalPrice(sport.getPricePerPerson() * request.getNumberOfPeople());

                    return ResponseEntity.ok(bookingRepository.save(booking));
                })
                .orElse(ResponseEntity.status(403).body("Unauthorized or booking not found"));
    }
//    @PostMapping("/cancel-booking/{id}")
//    public String cancelBooking(@PathVariable Long id, Principal principal) {
//        Booking booking = bookingRepository.findById(id).orElse(null);
//        if (booking != null && booking.getUser().getEmail().equals(principal.getName())) {
//            bookingRepository.delete(booking);
//        }
//        return "redirect:/my-bookings";
//    }

}
