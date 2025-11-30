package com.sc_fleetfinder.fleets.services.conversion_services.ListingReferenceDataConversionServices;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetMoonSystem;

public interface PlanetMoonSystemConversionService {

    PlanetMoonSystemDto convertToDto(PlanetMoonSystem planetMoonSystem);
    PlanetMoonSystem convertToEntity(PlanetMoonSystemDto planetMoonSystemDto);
}
