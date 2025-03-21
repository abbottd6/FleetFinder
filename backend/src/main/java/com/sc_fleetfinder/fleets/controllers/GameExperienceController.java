package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.services.CRUD_services.GameExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lookup/game-experiences")
public class GameExperienceController {

    @Autowired
    private GameExperienceService gameExperienceService;

    @GetMapping
    public List<GameExperienceDto> getAllGameExperiences() {
        return gameExperienceService.getAllExperiences();
    }

    @GetMapping("/{id}")
    public GameExperienceDto getGameExperienceById(@PathVariable Integer id) {
        return gameExperienceService.getExperienceById(id);
    }
}
