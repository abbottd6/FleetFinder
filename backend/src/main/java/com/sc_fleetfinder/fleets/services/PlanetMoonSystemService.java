package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;

import java.util.List;

public interface PlanetMoonSystemService {

    List<PlanetMoonSystemDto> getAllPlanetMoonSystems();
    PlanetMoonSystemDto getPlanetMoonSystemById(Integer id);
    PlanetMoonSystemDto convertToDto(PlanetMoonSystem entity);
}
