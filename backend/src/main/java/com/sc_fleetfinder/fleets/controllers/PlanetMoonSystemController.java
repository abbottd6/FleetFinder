package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.services.PlanetMoonSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/planetMoonSystems")
public class PlanetMoonSystemController {

    @Autowired
    private PlanetMoonSystemService planetMoonSystemService;

    @GetMapping
    public List<PlanetMoonSystemDto> getAllPlanetMoonSystems() {
        return planetMoonSystemService.getAllPlanetMoonSystems();
    }

    @GetMapping("/{id}")
    public PlanetMoonSystemDto getPlanetMoonSystemById(@PathVariable Integer id) {
        return planetMoonSystemService.getPlanetMoonSystemById(id);
    }
}
