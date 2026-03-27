package com.sc_fleetfinder.fleets.services.CRUD_services.ListingReferenceDataCRUD;

import com.sc_fleetfinder.fleets.DAO.ListingReferenceData.ServerRegionRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.ServerRegionCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServerRegionServiceImpl implements ServerRegionService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final ServerRegionCachingService serverRegionCachingService;
    private final ServerRegionRepository serverRegionRepository;

    public ServerRegionServiceImpl(ServerRegionCachingService serverRegionCachingService,
                                   ServerRegionRepository serverRegionRepository) {
        this.serverRegionCachingService = serverRegionCachingService;
        this.serverRegionRepository = serverRegionRepository;
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
                .orElseThrow(() -> {
                    log.error("Server Region requested but not found for Id: {}", id);
                    return new ResourceNotFoundException("Server Region requested but not found for Id: " + id);
                });
    }

    @Override
    public ServerRegion getServerEntityById(Integer id) {
        return serverRegionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Server Region requested but not found for Id: " + id)
        );
    }
}
