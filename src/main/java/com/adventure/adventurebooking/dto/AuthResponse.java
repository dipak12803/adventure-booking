package com.adventure.adventurebooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private String token; // Will return null for now, until we add JWT
}
