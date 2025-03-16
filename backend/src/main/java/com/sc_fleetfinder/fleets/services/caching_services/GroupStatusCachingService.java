package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupStatusDto;

import java.util.List;

public interface GroupStatusCachingService {

    List<GroupStatusDto> cacheAllGroupStatuses();
}
