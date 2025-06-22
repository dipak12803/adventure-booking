package com.adventure.adventurebooking.config;

import com.adventure.adventurebooking.entity.Slot;
import com.adventure.adventurebooking.entity.Sport;
import com.adventure.adventurebooking.repository.SlotRepository;
import com.adventure.adventurebooking.repository.SportRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SlotSeeder {

    private final SportRepository sportRepository;
    private final SlotRepository slotRepository;

    // Define fixed time slots
    private static final List<String> DEFAULT_TIME_SLOTS = Arrays.asList(
            "08:00 AM - 09:00 AM",
            "10:00 AM - 11:00 AM",
            "12:00 PM - 01:00 PM",
            "02:00 PM - 03:00 PM",
            "04:00 PM - 05:00 PM"
    );

    @PostConstruct
    public void seedSlots() {
        List<Sport> sports = sportRepository.findAll();

        for (Sport sport : sports) {
            List<Slot> existingSlots = slotRepository.findBySport(sport);

            if (existingSlots.isEmpty()) {
                for (String time : DEFAULT_TIME_SLOTS) {
                    Slot slot = Slot.builder()
                            .time(time)
                            .sport(sport)
                            .build();
                    slotRepository.save(slot);
                }
                System.out.println("Seeded slots for sport: " + sport.getName());
            }
        }
    }
}
