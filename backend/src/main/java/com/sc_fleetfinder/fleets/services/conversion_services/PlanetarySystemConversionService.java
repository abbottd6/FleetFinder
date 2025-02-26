package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;

public interface PlanetarySystemConversionService {

    PlanetarySystemDto convertToDto(PlanetarySystem planetarySystem);
    PlanetarySystem convertToEntity(PlanetarySystemDto dto);
}
