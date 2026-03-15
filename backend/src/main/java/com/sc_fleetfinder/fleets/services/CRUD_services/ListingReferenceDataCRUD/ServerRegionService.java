package com.sc_fleetfinder.fleets.services.CRUD_services.ListingReferenceDataCRUD;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;

import java.util.List;
import java.util.Optional;

public interface ServerRegionService {

    List<ServerRegionDto> getAllServerRegions();
    ServerRegionDto getServerRegionById(Integer id);
    ServerRegion getServerEntityById(Integer id);
}
