package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ServerRegionDto;

import java.util.List;

public interface ServerRegionService {

    List<ServerRegionDto> getAllServerRegions();
    ServerRegionDto getServerRegionById(int id);
}
