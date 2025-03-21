package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.services.CRUD_services.GameEnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lookup/game-environments")
public class GameEnvironmentsController {

    @Autowired
    private GameEnvironmentService gameEnvironmentService;

    @GetMapping
    public List<GameEnvironmentDto> getAllGameEnvironments() {
        return gameEnvironmentService.getAllEnvironments();
    }

    @GetMapping("/{id}")
    public GameEnvironmentDto getGameEnvironmentById(@PathVariable Integer id) {
        return gameEnvironmentService.getEnvironmentById(id);
    }
}
