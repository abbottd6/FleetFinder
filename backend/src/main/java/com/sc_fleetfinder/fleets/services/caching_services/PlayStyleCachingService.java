package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlayStyleDto;

import java.util.List;

public interface PlayStyleCachingService {

    List<PlayStyleDto> cacheAllPlayStyles();
}
