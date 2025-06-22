package com.adventure.adventurebooking.controller;

import com.adventure.adventurebooking.dto.RegisterRequest;
import com.adventure.adventurebooking.entity.User;
import com.adventure.adventurebooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "redirect:/register?error=email";
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        userRepository.save(user);
        return "redirect:/login?success";
    }
}
