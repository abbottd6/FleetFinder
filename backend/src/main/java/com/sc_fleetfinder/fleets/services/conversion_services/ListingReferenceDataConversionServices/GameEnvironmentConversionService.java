package com.sc_fleetfinder.fleets.services.conversion_services.ListingReferenceDataConversionServices;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameEnvironment;

public interface GameEnvironmentConversionService {

    GameEnvironmentDto convertToDto(GameEnvironment gameEnvironment);
    GameEnvironment convertToEntity(GameEnvironmentDto gameEnvironmentDto);
}
