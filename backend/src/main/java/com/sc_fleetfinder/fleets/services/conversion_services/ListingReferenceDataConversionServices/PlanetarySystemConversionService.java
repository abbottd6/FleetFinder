package com.sc_fleetfinder.fleets.services.conversion_services.ListingReferenceDataConversionServices;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetarySystem;

public interface PlanetarySystemConversionService {

    PlanetarySystemDto convertToDto(PlanetarySystem planetarySystem);
    PlanetarySystem convertToEntity(PlanetarySystemDto dto);
}
