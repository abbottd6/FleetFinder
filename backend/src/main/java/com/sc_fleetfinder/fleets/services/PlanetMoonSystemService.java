package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.PlanetMoonSystemDto;

import java.util.List;

public interface PlanetMoonSystemService {

    List<PlanetMoonSystemDto> getAllPlanetMoonSystems();
    PlanetMoonSystemDto getPlanetMoonSystemById(int id);
}
