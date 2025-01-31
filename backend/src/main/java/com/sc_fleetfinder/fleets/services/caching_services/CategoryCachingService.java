package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;

import java.util.List;

public interface CategoryCachingService {

    List<GameplayCategoryDto> cacheAllCategories ();
}
