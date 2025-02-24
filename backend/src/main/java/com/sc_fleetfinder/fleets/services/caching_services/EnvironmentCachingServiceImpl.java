package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.GameEnvironmentConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnvironmentCachingServiceImpl implements EnvironmentCachingService {

    private final EnvironmentRepository environmentRepository;
    private final GameEnvironmentConversionService gameEnvironmentConversionService;

    @Autowired
    public EnvironmentCachingServiceImpl(EnvironmentRepository environmentRepository,
                                         GameEnvironmentConversionService gameEnvironmentConversionService) {
        this.environmentRepository = environmentRepository;
        this.gameEnvironmentConversionService = gameEnvironmentConversionService;
    }

    @Override
    @Cacheable(value = "environmentsCache", key = "'allEnvironmentsCache'")
    public List<GameEnvironmentDto> cacheAllEnvironments() {
        List<GameEnvironment> environments = environmentRepository.findAll();

        if (environments.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access game environments data");
        }
        return environments.stream()
                .map(gameEnvironmentConversionService::convertToDto)
                .collect(Collectors.toList());
    }
}
