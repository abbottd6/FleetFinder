package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.PlanetMoonSystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.PlanetMoonSystemCachingService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanetMoonSystemServiceImpl implements PlanetMoonSystemService {

    private static final Logger logger = LoggerFactory.getLogger(PlanetMoonSystemServiceImpl.class);
    private final PlanetMoonSystemRepository planetMoonSystemRepository;
    private final PlanetMoonSystemCachingService planetMoonSystemCachingService;
    private final ModelMapper modelMapper;

    public PlanetMoonSystemServiceImpl(PlanetMoonSystemRepository planetMoonSystemRepository,
                                       PlanetMoonSystemCachingService planetMoonSystemCachingService) {
        super();
        this.planetMoonSystemCachingService = planetMoonSystemCachingService;
        this.planetMoonSystemRepository = planetMoonSystemRepository;
        this.modelMapper = new ModelMapper();
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
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Override
    public PlanetMoonSystemDto convertToDto(PlanetMoonSystem entity) {
        if(entity == null) {
            logger.error("Received null entity while converting PlanetMoonSystem to Dto for caching.");
            return null;
        }

        boolean isInvalid = false;
        //checking for valid id
        if (entity.getPlanetId() == null || entity.getPlanetId() == 0) {
            logger.error("Received null planet id while converting PlanetMoonSystem to Dto for caching.");
            isInvalid = true;
        }
        //checking for valid planet name
        if (entity.getPlanetName() == null || entity.getPlanetName().isEmpty()) {
            logger.error("Received null planet name while converting PlanetMoonSystem to Dto for caching.");
            isInvalid = true;
        }
        //verifying planetary system field is not null
        if(entity.getPlanetarySystem() == null) {
            logger.error("Received null PlanetarySystem field while converting PlanetMoonSystem to Dto for caching.");
            isInvalid = true;
        }
        //checking for usable planetary system data
        if(entity.getPlanetarySystem().getSystemName() == null || entity.getPlanetarySystem().getSystemName().isEmpty()) {
            logger.error("Received null or empty planetary system name while converting planetMoonSystem to Dto for caching.");
            isInvalid = true;
        }

        if(isInvalid) {
            return null;
        }

        try {
            return modelMapper.map(entity, PlanetMoonSystemDto.class);
        } catch(Exception ex) {
            logger.error("Failed to convert PlanetMoonSystem entity with ID {}: {}", entity.getPlanetId(),
                    ex.getMessage());
            return null;
        }
    }
    //this method is never used in v1 and is unlikely to be used later
    public PlanetMoonSystem convertToEntity(PlanetMoonSystemDto dto) {
        //checking repository for entity matching Dto id
        PlanetMoonSystem entity = planetMoonSystemRepository.findById(dto.getPlanetId())
                .orElseThrow(() -> new ResourceNotFoundException("PlanetMoonSystem with ID: " + dto.getPlanetId()
                + " not found"));
        //verifying name/id match for dto and entity
        if(!Objects.equals(dto.getPlanetName(), entity.getPlanetName())) {
            logger.error("Received wrong planet name while converting PlanetMoonSystem to Entity");
            //changing name to match the name of the entity with the same id if names don't match
            dto.setPlanetName(entity.getPlanetName());
        }
        if(!Objects.equals(dto.getSystemName(), entity.getPlanetarySystem().getSystemName())) {
            logger.error("Received wrong system name while converting PlanetMoonSystem to Entity");
            //changing system name to match the name of the entity with the same id if names don't match
            dto.setSystemName(entity.getPlanetarySystem().getSystemName());
        }
        return entity;
    }
}
