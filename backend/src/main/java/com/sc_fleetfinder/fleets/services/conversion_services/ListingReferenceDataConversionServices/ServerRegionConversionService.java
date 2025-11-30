package com.sc_fleetfinder.fleets.services.conversion_services.ListingReferenceDataConversionServices;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;

public interface ServerRegionConversionService {

    ServerRegionDto convertToDto(ServerRegion entity);
    ServerRegion convertToEntity(ServerRegionDto dto);
}
