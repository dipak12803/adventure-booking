package com.adventure.adventurebooking.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {

    private Long sportId;
    private int numberOfPeople;
    private LocalDate bookingDate;
    private String slotTime;
}
