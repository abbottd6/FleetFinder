package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.PlanetarySystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanetarySystemCachingServiceImpl implements PlanetarySystemCachingService {

    private final PlanetarySystemRepository planetarySystemRepository;
    private final PlanetarySystemService planetarySystemService;

    @Autowired
    public PlanetarySystemCachingServiceImpl(PlanetarySystemRepository planetarySystemRepository,
                                             PlanetarySystemService planetarySystemservice) {

        this.planetarySystemRepository = planetarySystemRepository;
        this.planetarySystemService = planetarySystemservice;
    }

    @Override
    @Cacheable(value = "planetarySystemsCache", key = "'allSystemsCache'")
    public List<PlanetarySystemDto> cacheAllPlanetarySystems() {
        List<PlanetarySystem> planetarySystems = planetarySystemRepository.findAll();

        if(planetarySystems.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for planetary systems");
        }

        return planetarySystems.stream()
                .map(planetarySystemService::convertToDto)
                .collect(Collectors.toList());
    }
}
