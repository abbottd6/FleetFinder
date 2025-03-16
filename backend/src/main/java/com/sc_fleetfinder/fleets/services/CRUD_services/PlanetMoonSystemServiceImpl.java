package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.PlanetMoonSystemCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanetMoonSystemServiceImpl implements PlanetMoonSystemService {

    private static final Logger log = LoggerFactory.getLogger(PlanetMoonSystemServiceImpl.class);
    private final PlanetMoonSystemCachingService planetMoonSystemCachingService;

    public PlanetMoonSystemServiceImpl(PlanetMoonSystemCachingService planetMoonSystemCachingService) {
        this.planetMoonSystemCachingService = planetMoonSystemCachingService;
    }

    @Override
    public List<PlanetMoonSystemDto> getAllPlanetMoonSystems() {
        return planetMoonSystemCachingService.cacheAllPlanetMoonSystems();
    }

    @Override
    public PlanetMoonSystemDto getPlanetMoonSystemById(Integer id) {
        List<PlanetMoonSystemDto> cachedPlanets = planetMoonSystemCachingService.cacheAllPlanetMoonSystems();

        return cachedPlanets.stream()
                .filter(planet -> planet.getPlanetId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("No planet moon system found with id {} in the cached planet moon systems", id);
                    return new ResourceNotFoundException(id);
                });
    }
}
