package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.PlanetarySystemCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanetarySystemServiceImpl implements PlanetarySystemService {

    private static final Logger log = LoggerFactory.getLogger(PlanetarySystemServiceImpl.class);
    private final PlanetarySystemCachingService planetarySystemCachingService;

    public PlanetarySystemServiceImpl(PlanetarySystemCachingService planetarySystemCachingService) {
        this.planetarySystemCachingService = planetarySystemCachingService;
    }

    @Override
    public List<PlanetarySystemDto> getAllPlanetarySystems() {
        return planetarySystemCachingService.cacheAllPlanetarySystems();
    }

    @Override
    public PlanetarySystemDto getPlanetarySystemById(Integer id) {
        List<PlanetarySystemDto> cachedPlanetarySystems = planetarySystemCachingService.cacheAllPlanetarySystems();

        return cachedPlanetarySystems.stream()
                .filter(system -> system.getSystemId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }
}
