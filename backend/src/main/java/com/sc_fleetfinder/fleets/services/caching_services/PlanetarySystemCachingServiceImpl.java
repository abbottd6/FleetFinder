package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.PlanetarySystemService;
import com.sc_fleetfinder.fleets.services.conversion_services.PlanetarySystemConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanetarySystemCachingServiceImpl implements PlanetarySystemCachingService {

    private static final Logger log = LoggerFactory.getLogger(PlanetarySystemCachingServiceImpl.class);
    private final PlanetarySystemRepository planetarySystemRepository;
    private final PlanetarySystemConversionService planetarySystemConversionService;

    @Autowired
    public PlanetarySystemCachingServiceImpl(PlanetarySystemRepository planetarySystemRepository,
                                             PlanetarySystemConversionService planetarySystemConversionService) {

        this.planetarySystemRepository = planetarySystemRepository;
        this.planetarySystemConversionService = planetarySystemConversionService;
    }

    @Override
    @Cacheable(value = "planetarySystemsCache", key = "'allSystemsCache'")
    public List<PlanetarySystemDto> cacheAllPlanetarySystems() {
        List<PlanetarySystem> planetarySystems = planetarySystemRepository.findAll();

        if(planetarySystems.isEmpty()) {
            log.error("Unable to access Planetary System data for caching.");
            throw new ResourceNotFoundException("Unable to access data for planetary systems");
        }

        return planetarySystems.stream()
                .map(planetarySystemConversionService::convertToDto)
                .collect(Collectors.toList());
    }
}
