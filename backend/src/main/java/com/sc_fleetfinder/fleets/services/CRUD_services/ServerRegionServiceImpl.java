package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.ServerRegionCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerRegionServiceImpl implements ServerRegionService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final ServerRegionCachingService serverRegionCachingService;

    public ServerRegionServiceImpl(ServerRegionCachingService serverRegionCachingService) {
        this.serverRegionCachingService = serverRegionCachingService;
    }

    @Override
    public List<ServerRegionDto> getAllServerRegions() {
        return serverRegionCachingService.cacheAllServerRegions();
    }

    @Override
    public ServerRegionDto getServerRegionById(Integer id) {
        List<ServerRegionDto> cachedServerRegions = serverRegionCachingService.cacheAllServerRegions();

        return cachedServerRegions.stream()
                .filter(server -> server.getServerId().equals(id))
                .findFirst()
                .orElseGet(() -> {
                    log.error("Server Region requested but not found for Id: {}", id);
                    throw new ResourceNotFoundException("Server Region requested but not found for Id: " +
                            id);
                });
    }
}
