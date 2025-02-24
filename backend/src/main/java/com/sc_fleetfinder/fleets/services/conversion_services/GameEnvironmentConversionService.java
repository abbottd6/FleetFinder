package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;

public interface GameEnvironmentConversionService {

    GameEnvironmentDto convertToDto(GameEnvironment gameEnvironment);
    GameEnvironment convertToEntity(GameEnvironmentDto gameEnvironmentDto);
}
