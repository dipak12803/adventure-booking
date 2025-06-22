package com.adventure.adventurebooking.controller;

import com.adventure.adventurebooking.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthPageController {

    private final SportRepository sportRepository;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new com.adventure.adventurebooking.dto.RegisterRequest());
        return "register";
    }

//    @GetMapping("/home")
//    public String homePage(Model model) {
//        model.addAttribute("sports", sportRepository.findAll());
//        return "home";
//    }
}
