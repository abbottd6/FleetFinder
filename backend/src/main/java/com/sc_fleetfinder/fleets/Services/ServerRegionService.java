package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.ServerRegionDto;
import com.sc_fleetfinder.fleets.entities.ServerRegion;

import java.util.List;

public interface ServerRegionService {

    List<ServerRegionDto> getAllServerRegions();
    ServerRegionDto getServerRegionById(int id);
}
