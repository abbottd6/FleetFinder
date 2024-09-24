package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.entities.ServerRegion;

import java.util.List;

public interface ServerRegionService {

    List<ServerRegion> getAllServerRegions();
    ServerRegion getServerRegionById(int id);
}
