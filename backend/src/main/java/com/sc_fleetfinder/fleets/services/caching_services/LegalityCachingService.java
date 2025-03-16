package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;

import java.util.List;

public interface LegalityCachingService {

    List<LegalityDto> cacheAllLegalities();
}
