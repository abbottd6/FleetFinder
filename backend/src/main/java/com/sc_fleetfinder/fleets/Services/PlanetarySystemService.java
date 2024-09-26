package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;

import java.util.List;

public interface PlanetarySystemService {

    List<PlanetarySystemDto> getAllPlanetarySystems();
    PlanetarySystemDto getPlanetarySystemById(int id);

}
