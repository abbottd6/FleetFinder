package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.PlanetarySystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetarySystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetarySystem;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PlanetarySystemConversionServiceImpl implements PlanetarySystemConversionService {

    private static final Logger log = LoggerFactory.getLogger(PlanetarySystemConversionServiceImpl.class);
    private final PlanetarySystemRepository planetarySystemRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PlanetarySystemConversionServiceImpl(PlanetarySystemRepository planetarySystemRepository) {
        this.planetarySystemRepository = planetarySystemRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
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

    @Override
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
