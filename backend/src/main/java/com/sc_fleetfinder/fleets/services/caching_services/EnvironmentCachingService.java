package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;

import java.util.List;

public interface EnvironmentCachingService {

    List<GameEnvironmentDto> cacheAllEnvironments();
}
