package com.sc_fleetfinder.fleets.services.conversion_services.ListingReferenceDataConversionServices;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.Legality;

public interface LegalityConversionService {

    LegalityDto convertToDto(Legality legality);
    Legality convertToEntity(LegalityDto dto);
}
