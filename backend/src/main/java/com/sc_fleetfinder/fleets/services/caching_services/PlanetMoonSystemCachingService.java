package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;

import java.util.List;

public interface PlanetMoonSystemCachingService {
    List<PlanetMoonSystemDto> cacheAllPlanetMoonSystems();
}
