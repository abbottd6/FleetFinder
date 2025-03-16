package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.entities.GameExperience;

public interface GameExperienceConversionService {

    GameExperienceDto convertToDto(GameExperience gameExperience);
    GameExperience convertToEntity(GameExperienceDto gameExperienceDto);
}
