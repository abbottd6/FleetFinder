package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.services.PlanetarySystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/planetarySystems")
public class PlanetarySystemController {

    @Autowired
    private PlanetarySystemService planetarySystemService;

    @GetMapping
    public List<PlanetarySystemDto> getAllPlanetarySystems() {
        return planetarySystemService.getAllPlanetarySystems();
    }

    @GetMapping("/{id}")
    public PlanetarySystemDto getPlanetarySystemById(@PathVariable Integer id) {
        return planetarySystemService.getPlanetarySystemById(id);
    }
}
