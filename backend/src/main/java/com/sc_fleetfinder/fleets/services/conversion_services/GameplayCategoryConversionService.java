package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;

public interface GameplayCategoryConversionService {

    GameplayCategoryDto convertToDto(GameplayCategory gameplayCategory);
    GameplayCategory convertToEntity(GameplayCategoryDto gameplayCategoryDto);
}
