package com.sc_fleetfinder.fleets.services.conversion_services.ListingReferenceDataConversionServices;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameExperience;

public interface GameExperienceConversionService {

    GameExperienceDto convertToDto(GameExperience gameExperience);
    GameExperience convertToEntity(GameExperienceDto gameExperienceDto);
}
