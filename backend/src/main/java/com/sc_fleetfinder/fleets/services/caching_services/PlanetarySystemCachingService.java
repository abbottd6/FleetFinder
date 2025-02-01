package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;

import java.util.List;

public interface PlanetarySystemCachingService {
    List<PlanetarySystemDto> cacheAllPlanetarySystems();
}
