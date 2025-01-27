package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;

import java.util.List;

public interface PlanetarySystemService {

    List<PlanetarySystemDto> getAllPlanetarySystems();
    PlanetarySystemDto getPlanetarySystemById(Integer id);

}
