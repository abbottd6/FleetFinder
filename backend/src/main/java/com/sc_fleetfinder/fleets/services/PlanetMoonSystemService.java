package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;

import java.util.List;

public interface PlanetMoonSystemService {

    List<PlanetMoonSystemDto> getAllPlanetMoonSystems();
    PlanetMoonSystemDto getPlanetMoonSystemById(Integer id);
}
