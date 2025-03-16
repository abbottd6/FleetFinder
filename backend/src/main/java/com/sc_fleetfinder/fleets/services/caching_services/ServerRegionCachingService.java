package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ServerRegionDto;

import java.util.List;

public interface ServerRegionCachingService {

    List<ServerRegionDto> cacheAllServerRegions();
}
