package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlayStyleDto;
import com.sc_fleetfinder.fleets.entities.PlayStyle;

public interface PlayStyleConversionService {

    PlayStyleDto convertToDto(PlayStyle playStyle);
    PlayStyle convertToEntity(PlayStyleDto playStyleDto);
}
