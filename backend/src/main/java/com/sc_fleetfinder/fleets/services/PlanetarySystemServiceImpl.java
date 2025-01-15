package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import org.modelmapper.ModelMapper;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanetarySystemServiceImpl implements PlanetarySystemService {

    private final PlanetarySystemRepository planetarySystemRepository;
    private final ModelMapper modelMapper;

    public PlanetarySystemServiceImpl(PlanetarySystemRepository planetarySystemRepository) {
        super();
        this.planetarySystemRepository = planetarySystemRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public List<PlanetarySystemDto> getAllPlanetarySystems() {
        List<PlanetarySystem> planetarySystems = planetarySystemRepository.findAll();
        return planetarySystems.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlanetarySystemDto getPlanetarySystemById(int id) {
        Optional<PlanetarySystem> planetarySystem = planetarySystemRepository.findById(id);
        if(planetarySystem.isPresent()) {
            return convertToDto(planetarySystem.get());
        }
        else {
            throw new ResourceNotFoundException("Planetary system with id " + id + " not found");
        }
    }

    public PlanetarySystemDto convertToDto(PlanetarySystem entity) {
        return modelMapper.map(entity, PlanetarySystemDto.class);
    }

    public PlanetarySystem convertToEntity(PlanetarySystemDto dto) {
        return modelMapper.map(dto, PlanetarySystem.class);
    }
}
