package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.ServerRegionRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.entities.ServerRegion;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.ServerRegionConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServerRegionCachingServiceImpl implements ServerRegionCachingService {

    private static final Logger log = LoggerFactory.getLogger(ServerRegionCachingServiceImpl.class);
    private final ServerRegionRepository serverRegionRepository;
    private final ServerRegionConversionService serverRegionConversionService;

    @Autowired
    public ServerRegionCachingServiceImpl(ServerRegionRepository serverRegionRepository,
                                          ServerRegionConversionService serverRegionConversionService) {
        this.serverRegionRepository = serverRegionRepository;
        this.serverRegionConversionService = serverRegionConversionService;
    }

    @Override
    @Cacheable(value = "serverRegionsCache", key = "'allServerRegionsCache'")
    public List<ServerRegionDto> cacheAllServerRegions() {
        List<ServerRegion> serverRegions = serverRegionRepository.findAll();

        if(serverRegions.isEmpty()) {
            log.error("Unable to access server regions data for caching.");
            throw new ResourceNotFoundException("Unable to access server region data");
        }

        return serverRegions.stream()
                .map(serverRegionConversionService::convertToDto)
                .collect(Collectors.toList());
    }
}
