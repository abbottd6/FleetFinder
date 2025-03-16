package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.entities.Legality;

public interface LegalityConversionService {

    LegalityDto convertToDto(Legality legality);
    Legality convertToEntity(LegalityDto dto);
}
