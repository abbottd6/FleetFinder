package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanetarySystemServiceImpl implements PlanetarySystemService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final PlanetarySystemRepository planetarySystemRepository;
    private final ModelMapper modelMapper;

    public PlanetarySystemServiceImpl(PlanetarySystemRepository planetarySystemRepository) {
        super();
        this.planetarySystemRepository = planetarySystemRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    @Cacheable(value = "planetarySystemsCache", key = "'allSystemsCache'")
    public List<PlanetarySystemDto> getAllPlanetarySystems() {
        log.info("Caching test: getting all systems");
        List<PlanetarySystem> planetarySystems = planetarySystemRepository.findAll();

        if(planetarySystems.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for planetary systems");
        }

        return planetarySystems.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlanetarySystemDto getPlanetarySystemById(Integer id) {
        Optional<PlanetarySystem> planetarySystem = planetarySystemRepository.findById(id);
        if(planetarySystem.isPresent()) {
            return convertToDto(planetarySystem.get());
        }
        else {
            throw new ResourceNotFoundException(id);
        }
    }

    public PlanetarySystemDto convertToDto(PlanetarySystem entity) {
        return modelMapper.map(entity, PlanetarySystemDto.class);
    }

    public PlanetarySystem convertToEntity(PlanetarySystemDto dto) {
        return modelMapper.map(dto, PlanetarySystem.class);
    }
}
