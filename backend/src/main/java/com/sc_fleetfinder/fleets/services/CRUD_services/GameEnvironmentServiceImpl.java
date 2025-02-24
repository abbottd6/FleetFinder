package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.EnvironmentCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameEnvironmentServiceImpl implements GameEnvironmentService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final EnvironmentCachingService environmentCachingService;

    public GameEnvironmentServiceImpl(EnvironmentCachingService environmentCachingService) {
        super();
        this.environmentCachingService = environmentCachingService;
    }

    @Override
    public List<GameEnvironmentDto> getAllEnvironments() {
        return environmentCachingService.cacheAllEnvironments();
    }

    @Override
    public GameEnvironmentDto getEnvironmentById(Integer id) {
        List<GameEnvironmentDto> cachedEnvironments = environmentCachingService.cacheAllEnvironments();

        return cachedEnvironments.stream()
                .filter(environment -> environment.getEnvironmentId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }
}
