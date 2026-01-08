package com.sc_fleetfinder.fleets.services.conversion_services.ListingReferenceDataConversionServices;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.PvpStatusDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PvpStatus;

public interface PvpStatusConversionService {

    PvpStatusDto convertToDto(PvpStatus pvpStatus);
    PvpStatus convertToEntity(PvpStatusDto pvpStatusDto);
}
