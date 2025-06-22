package com.adventure.adventurebooking.repository;

import com.adventure.adventurebooking.entity.Slot;
import com.adventure.adventurebooking.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findBySport(Sport sport);
}
