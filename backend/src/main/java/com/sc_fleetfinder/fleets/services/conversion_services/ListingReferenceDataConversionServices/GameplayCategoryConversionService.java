package com.sc_fleetfinder.fleets.services.conversion_services.ListingReferenceDataConversionServices;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplayCategory;

public interface GameplayCategoryConversionService {

    GameplayCategoryDto convertToDto(GameplayCategory gameplayCategory);
    GameplayCategory convertToEntity(GameplayCategoryDto gameplayCategoryDto);
}
