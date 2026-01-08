package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.GameExperienceDto;

import java.util.List;

public interface ExperienceCachingService {

    List<GameExperienceDto> cacheAllExperiences();
}
