package com.adventure.adventurebooking.repository;

import com.adventure.adventurebooking.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportRepository extends JpaRepository<Sport, Long> {
}
