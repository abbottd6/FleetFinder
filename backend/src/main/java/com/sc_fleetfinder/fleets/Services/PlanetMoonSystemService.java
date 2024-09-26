package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;

import java.util.List;

public interface PlanetMoonSystemService {

    List<PlanetMoonSystemDto> getAllPlanetMoonSystems();
    PlanetMoonSystemDto getPlanetMoonSystemById(int id);
}
