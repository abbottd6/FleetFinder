package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.PlanetarySystemCachingService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PlanetarySystemServiceImpl implements PlanetarySystemService {

    private static final Logger log = LoggerFactory.getLogger(PlanetarySystemServiceImpl.class);
    private final PlanetarySystemRepository planetarySystemRepository;
    private final PlanetarySystemCachingService planetarySystemCachingService;
    private final ModelMapper modelMapper;

    public PlanetarySystemServiceImpl(PlanetarySystemRepository planetarySystemRepository,
                                      PlanetarySystemCachingService planetarySystemCachingService) {
        super();
        this.planetarySystemCachingService = planetarySystemCachingService;
        this.planetarySystemRepository = planetarySystemRepository;
        this.modelMapper = new ModelMapper();
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

    public PlanetarySystemDto convertToDto(PlanetarySystem entity) {
        //id valid check
        if(entity.getSystemId() == null || entity.getSystemId() == 0) {
            throw new ResourceNotFoundException("Planetary system id is null or empty");
        }
        //type/name valid check
        if(entity.getSystemName() == null || entity.getSystemName().isEmpty()) {
            throw new ResourceNotFoundException("Planetary system name is null or empty");
        }

        return modelMapper.map(entity, PlanetarySystemDto.class);
    }

    public PlanetarySystem convertToEntity(PlanetarySystemDto dto) {
        //checking repository for entity matching Dto id
        PlanetarySystem entity = planetarySystemRepository.findById(dto.getSystemId())
                .orElseThrow(() -> new ResourceNotFoundException("Planetary system with ID: " + dto.getSystemId() +
                " not found"));
        //verifying name/id match for dto and entity
        if(!Objects.equals(dto.getSystemName(), entity.getSystemName())) {
            throw new ResourceNotFoundException("System name mismatch for Dto: " + dto.getSystemName() +
                    ", ID: " + dto.getSystemId() + " and entity: " + entity.getSystemName() + ", and ID: " +
                    entity.getSystemId());
        }
        //if Id exists and names match then returns entity
        return entity;
    }
}
