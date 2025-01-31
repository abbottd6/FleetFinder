package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.GameEnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnvironmentCachingServiceImpl implements EnvironmentCachingService {

    private final EnvironmentRepository environmentRepository;
    private final GameEnvironmentService gameEnvironmentService;

    @Autowired
    public EnvironmentCachingServiceImpl(EnvironmentRepository environmentRepository
            , GameEnvironmentService gameEnvironmentService) {
        this.environmentRepository = environmentRepository;
        this.gameEnvironmentService = gameEnvironmentService;
    }

    @Override
    @Cacheable(value = "environmentsCache", key = "'allEnvironmentsCache'")
    public List<GameEnvironmentDto> cacheAllEnvironments() {
        List<GameEnvironment> environments = environmentRepository.findAll();

        if (environments.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access game environments data");
        }
        return environments.stream()
                .map(gameEnvironmentService::convertToDto)
                .collect(Collectors.toList());
    }
}
