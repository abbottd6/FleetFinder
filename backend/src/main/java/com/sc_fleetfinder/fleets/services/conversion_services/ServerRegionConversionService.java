package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.entities.ServerRegion;

public interface ServerRegionConversionService {

    ServerRegionDto convertToDto(ServerRegion entity);
    ServerRegion convertToEntity(ServerRegionDto dto);
}
