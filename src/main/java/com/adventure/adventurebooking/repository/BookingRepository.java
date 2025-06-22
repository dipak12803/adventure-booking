package com.adventure.adventurebooking.repository;

import com.adventure.adventurebooking.entity.Booking;
import com.adventure.adventurebooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);
}
