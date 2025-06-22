package com.adventure.adventurebooking.initializer;

import com.adventure.adventurebooking.entity.Sport;
import com.adventure.adventurebooking.repository.SportRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class SportInitializer implements ApplicationRunner {

    private final SportRepository sportRepository;

    public SportInitializer(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (sportRepository.count() == 0) {
            loadSportsFromJson();
        }
    }

    private void loadSportsFromJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ClassPathResource("data/sports.json").getInputStream();
        JsonNode rootNode = mapper.readTree(is);

        List<Sport> sports = new ArrayList<>();

        for (JsonNode node : rootNode) {
            Sport sport = new Sport();
            sport.setName(node.get("name").asText());
            sport.setDescription(node.get("description").asText());
            sport.setAvailableSlots(node.get("availableSlots").asInt());
            sport.setPricePerPerson(node.get("pricePerPerson").asInt());
            sport.setImageName(node.get("imageName").asText());

            sports.add(sport);
        }

        sportRepository.saveAll(sports);
    }
}
