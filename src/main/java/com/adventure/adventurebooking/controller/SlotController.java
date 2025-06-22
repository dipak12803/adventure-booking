package com.adventure.adventurebooking.controller;

import com.adventure.adventurebooking.entity.Slot;
import com.adventure.adventurebooking.entity.Sport;
import com.adventure.adventurebooking.repository.SlotRepository;
import com.adventure.adventurebooking.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
public class SlotController {

    private final SlotRepository slotRepository;
    private final SportRepository sportRepository;

    // Get slots for a sport
    @GetMapping("/{sportId}")
    public ResponseEntity<List<Slot>> getSlotsBySport(@PathVariable Long sportId) {
        Sport sport = sportRepository.findById(sportId).orElse(null);
        if (sport == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(slotRepository.findBySport(sport));
    }

    // Admin: Add slot to sport
    @PostMapping("/{sportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Slot> addSlot(@PathVariable Long sportId, @RequestBody Slot slot) {
        Sport sport = sportRepository.findById(sportId).orElse(null);
        if (sport == null) return ResponseEntity.notFound().build();

        slot.setSport(sport);
        return ResponseEntity.ok(slotRepository.save(slot));
    }

    // Admin: Delete a slot
    @DeleteMapping("/{slotId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long slotId) {
        slotRepository.deleteById(slotId);
        return ResponseEntity.ok().build();
    }
}
