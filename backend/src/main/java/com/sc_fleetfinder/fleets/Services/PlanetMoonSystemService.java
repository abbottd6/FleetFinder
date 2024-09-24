package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;

import java.util.List;

public interface PlanetMoonSystemService {

    List<PlanetMoonSystem> getAllPlanetMoonSystems();
    PlanetMoonSystem getPlanetMoonSystemById(int id);
}
