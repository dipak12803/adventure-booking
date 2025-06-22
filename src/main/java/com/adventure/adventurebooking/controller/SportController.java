package com.adventure.adventurebooking.controller;

import com.adventure.adventurebooking.entity.Sport;
import com.adventure.adventurebooking.repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import lombok.*;
@RestController
@RequestMapping("/api/sports")
@Builder
public class SportController {

    @Autowired
    private SportRepository sportRepository;

    private final String UPLOAD_DIR = "src/main/resources/static/images/";

    @PostMapping("/add")
    public String addSport(
            @RequestParam("iname") String name,
            @RequestParam("description") String description,
            @RequestParam("availableSlots") int availableSlots,
            @RequestParam("price") double price,
            @RequestParam("image") MultipartFile image
    ) throws IOException {

        System.out.println("Received sport data for: " + name);

        String imageName = StringUtils.cleanPath(image.getOriginalFilename());
        File uploadDir = new File(UPLOAD_DIR);

        if (!uploadDir.exists()) uploadDir.mkdirs();

        File imageFile = new File(UPLOAD_DIR + imageName);
        image.transferTo(imageFile);

        System.out.println("Image saved to: " + imageFile.getAbsolutePath());

        Sport sport = Sport.builder()
                .name(name)
                .description(description)
                .availableSlots(availableSlots)
                .pricePerPerson((int) price)
                .imageName(imageName)
                .build();

        sportRepository.save(sport);
        System.out.println("Sport saved to DB");

        return "Sport added successfully";
    }
}
