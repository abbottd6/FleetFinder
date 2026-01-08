package com.sc_fleetfinder.fleets.services.CRUD_services.ListingReferenceDataCRUD;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.GameEnvironmentDto;

import java.util.List;

public interface GameEnvironmentService {

    List<GameEnvironmentDto> getAllEnvironments();
    GameEnvironmentDto getEnvironmentById(Integer id);
}
