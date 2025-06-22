package com.adventure.adventurebooking.config;

import com.adventure.adventurebooking.entity.Slot;
import com.adventure.adventurebooking.entity.Sport;
import com.adventure.adventurebooking.repository.SlotRepository;
import com.adventure.adventurebooking.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SlotRepository slotRepository;
    private final SportRepository sportRepository;

    @Override
    public void run(String... args) {
        if (slotRepository.count() == 0) {
            List<String> defaultTimes = Arrays.asList(
                    "08:00 AM", "10:00 AM", "12:00 PM", "02:00 PM", "04:00 PM"
            );

            List<Sport> sports = sportRepository.findAll();

            for (Sport sport : sports) {
                for (String time : defaultTimes) {
                    Slot slot = Slot.builder()
                            .sport(sport)
                            .time(time)
                            .build();
                    slotRepository.save(slot);
                }
            }

            System.out.println("✅ Slots seeded for each sport.");
        }
    }
}
