package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.PlanetMoonSystemRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlanetMoonSystemDto;
import com.sc_fleetfinder.fleets.entities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PlanetMoonSystemConversionServiceImpl implements PlanetMoonSystemConversionService {

    private static final Logger logger = LoggerFactory.getLogger(PlanetMoonSystemConversionServiceImpl.class);
    private final PlanetMoonSystemRepository planetMoonSystemRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PlanetMoonSystemConversionServiceImpl(PlanetMoonSystemRepository planetMoonSystemRepository) {
        this.planetMoonSystemRepository = planetMoonSystemRepository;
        this.modelMapper = new ModelMapper();
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
    @Override
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
