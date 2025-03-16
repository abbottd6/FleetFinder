package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PvpStatusDto;

import java.util.List;

public interface PvpStatusCachingService {

    List<PvpStatusDto> cacheAllPvpStatuses();
}
