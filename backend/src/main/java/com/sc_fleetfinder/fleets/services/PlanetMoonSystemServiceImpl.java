package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.PlanetMoonSystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanetMoonSystemServiceImpl implements PlanetMoonSystemService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final PlanetMoonSystemRepository planetMoonSystemRepository;
    private final ModelMapper modelMapper;

    public PlanetMoonSystemServiceImpl(PlanetMoonSystemRepository planetMoonSystemRepository) {
        super();
        this.planetMoonSystemRepository = planetMoonSystemRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    @Cacheable(value = "planetsCache", key = "'allPlanetsCache'")
    public List<PlanetMoonSystemDto> getAllPlanetMoonSystems() {
        log.info("Caching test: getting all planet moon systems");
        List<PlanetMoonSystem> planetMoonSystems = planetMoonSystemRepository.findAll();
        return planetMoonSystems.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlanetMoonSystemDto getPlanetMoonSystemById(int id) {
        Optional<PlanetMoonSystem> optionalPlanetMoonSystem = planetMoonSystemRepository.findById(id);
        if (optionalPlanetMoonSystem.isPresent()) {
            return convertToDto(optionalPlanetMoonSystem.get());
        }
        else {
            throw new ResourceNotFoundException("PlanetMoonSystem with id " + id + " not found");
        }
    }

    public PlanetMoonSystemDto convertToDto(PlanetMoonSystem entity) {
        return modelMapper.map(entity, PlanetMoonSystemDto.class);
    }

    public PlanetMoonSystem convertToEntity(PlanetMoonSystemDto dto) {
        return modelMapper.map(dto, PlanetMoonSystem.class);
    }
}
