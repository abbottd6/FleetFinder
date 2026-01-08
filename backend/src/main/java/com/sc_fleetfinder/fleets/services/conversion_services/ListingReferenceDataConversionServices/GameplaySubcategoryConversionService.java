package com.sc_fleetfinder.fleets.services.conversion_services.ListingReferenceDataConversionServices;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplaySubcategory;

public interface GameplaySubcategoryConversionService {

    GameplaySubcategoryDto convertToDto(GameplaySubcategory gameplaySubcategory);
    GameplaySubcategory convertToEntity(GameplaySubcategoryDto gameplaySubcategoryDto);
}
