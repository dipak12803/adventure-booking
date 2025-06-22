package com.adventure.adventurebooking.controller;

import com.adventure.adventurebooking.dto.BookingRequest;
import com.adventure.adventurebooking.entity.Booking;
import com.adventure.adventurebooking.entity.Slot;
import com.adventure.adventurebooking.entity.Sport;
import com.adventure.adventurebooking.entity.User;
import com.adventure.adventurebooking.repository.BookingRepository;
import com.adventure.adventurebooking.repository.SlotRepository;
import com.adventure.adventurebooking.repository.SportRepository;
import com.adventure.adventurebooking.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final SportRepository sportRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final SlotRepository slotRepository;

    // Home page redirect
//    @GetMapping("/")
//    public String home() {
//        return "redirect:/sports";
//    }

    // Login form
//    @GetMapping("/login")
//    public String loginPage() {
//        return "login";
//    }

    // Register form
//    @GetMapping("/register")
//    public String registerPage() {
//        return "register";
//    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        List<Sport> sports = sportRepository.findAll();
        for (Sport sport : sports) {
            System.out.println("Loaded image: " + sport.getImageName()); // 👈 check value
        }
        model.addAttribute("sports", sportRepository.findAll());
        return "home";
    }

    // View all sports
    @GetMapping("/sports")
    public String viewSports(Model model) {
        List<Sport> sports = sportRepository.findAll();
        model.addAttribute("sports", sports);
        return "sports";
    }

    // Booking form page
    @GetMapping("/book/{id}")
    public String showBookingForm(@PathVariable Long id, Model model) {
        Sport sport = sportRepository.findById(id).orElse(null);
        if (sport == null) return "redirect:/sports";

        List<Slot> slots = slotRepository.findBySport(sport);

        model.addAttribute("sport", sport);
        model.addAttribute("slots", slots); // pass slot list to frontend
        return "book";
    }

    // Handle booking submission
    @PostMapping("/book")
    public String confirmBooking(@ModelAttribute BookingRequest request,
                                 @AuthenticationPrincipal org.springframework.security.core.userdetails.User springUser) {
        User user = userService.getUserByEmail(springUser.getUsername()); // ✅ fix here

        Sport sport = sportRepository.findById(request.getSportId()).orElse(null);
        if (sport == null || sport.getAvailableSlots() < request.getNumberOfPeople()) {
            return "redirect:/sports";
        }

        sport.setAvailableSlots(sport.getAvailableSlots() - request.getNumberOfPeople());
        sportRepository.save(sport);

        Booking booking = Booking.builder()
                .user(user)
                .sport(sport)
                .bookingDate(request.getBookingDate())
                .slotTime(request.getSlotTime())
                .numberOfPeople(request.getNumberOfPeople())
                .totalPrice(request.getNumberOfPeople() * sport.getPricePerPerson())
                .status("CONFIRMED")
                .build();

        bookingRepository.save(booking);
        return "redirect:/my-bookings";
    }


    // View logged-in user's bookings
    @GetMapping("/my-bookings")
    public String viewBookings(Model model,
                               @AuthenticationPrincipal org.springframework.security.core.userdetails.User springUser) {
        User user = userService.getUserByEmail(springUser.getUsername()); // ✅ fix here

        List<Booking> bookings = bookingRepository.findByUser(user);
        model.addAttribute("bookings", bookings);
        return "bookings";
    }
    @PostMapping("/cancel-booking/{id}")
    public String cancelBooking(@PathVariable Long id,
                                @AuthenticationPrincipal org.springframework.security.core.userdetails.User springUser) {
        Booking booking = bookingRepository.findById(id).orElse(null);

        if (booking != null && booking.getUser().getEmail().equals(springUser.getUsername())) {
            Sport sport = booking.getSport();

            // Reallocate slot
            sport.setAvailableSlots(sport.getAvailableSlots() + booking.getNumberOfPeople());
            sportRepository.save(sport);

            bookingRepository.delete(booking);
        }

        return "redirect:/my-bookings";
    }

}
