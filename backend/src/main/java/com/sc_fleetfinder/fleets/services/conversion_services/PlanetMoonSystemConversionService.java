package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;

public interface PlanetMoonSystemConversionService {

    PlanetMoonSystemDto convertToDto(PlanetMoonSystem planetMoonSystem);
    PlanetMoonSystem convertToEntity(PlanetMoonSystemDto planetMoonSystemDto);
}
