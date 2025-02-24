package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;

public interface GameplaySubcategoryConversionService {

    GameplaySubcategoryDto convertToDto(GameplaySubcategory gameplaySubcategory);
    GameplaySubcategory convertToEntity(GameplaySubcategoryDto gameplaySubcategoryDto);
}
