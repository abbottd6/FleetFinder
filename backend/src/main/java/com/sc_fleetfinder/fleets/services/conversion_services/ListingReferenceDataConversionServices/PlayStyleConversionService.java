package com.sc_fleetfinder.fleets.services.conversion_services.ListingReferenceDataConversionServices;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.PlayStyleDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlayStyle;

public interface PlayStyleConversionService {

    PlayStyleDto convertToDto(PlayStyle playStyle);
    PlayStyle convertToEntity(PlayStyleDto playStyleDto);
}
