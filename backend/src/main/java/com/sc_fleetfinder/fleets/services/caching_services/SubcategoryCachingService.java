package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;

import java.util.List;

public interface SubcategoryCachingService {

    List<GameplaySubcategoryDto> cacheAllSubcategories();
}
