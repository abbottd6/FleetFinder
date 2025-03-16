package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.PlanetMoonSystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.PlanetMoonSystemService;
import com.sc_fleetfinder.fleets.services.conversion_services.PlanetMoonSystemConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanetMoonSystemCachingServiceImpl implements PlanetMoonSystemCachingService {

    private static final Logger log = LoggerFactory.getLogger(PlanetMoonSystemCachingServiceImpl.class);
    private final PlanetMoonSystemRepository planetMoonSystemRepository;
    private final PlanetMoonSystemConversionService planetMoonSystemConversionService;

    @Autowired
    public PlanetMoonSystemCachingServiceImpl(PlanetMoonSystemRepository planetMoonSystemRepository,
                                              PlanetMoonSystemConversionService planetMoonSystemConversionService) {
        this.planetMoonSystemRepository = planetMoonSystemRepository;
        this.planetMoonSystemConversionService = planetMoonSystemConversionService;
    }

    @Override
    @Cacheable(value = "planetsCache", key = "'allPlanetsCache'")
    public List<PlanetMoonSystemDto> cacheAllPlanetMoonSystems() {
        List<PlanetMoonSystem> planetMoonSystems = planetMoonSystemRepository.findAll();

        if(planetMoonSystems.isEmpty()) {
            log.error("Unable to access planet moon system data for caching");
            throw new ResourceNotFoundException("Unable to access data for planet/moon systems");
        }

        return planetMoonSystems.stream()
                .map(planetMoonSystemConversionService::convertToDto)
                .collect(Collectors.toList());
    }
}
