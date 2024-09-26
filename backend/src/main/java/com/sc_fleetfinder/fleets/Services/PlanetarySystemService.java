package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.entities.PlanetarySystem;

import java.util.List;

public interface PlanetarySystemService {

    List<PlanetarySystem> getAllPlanetarySystems();
    PlanetarySystem getPlanetarySystemById(int id);

}
