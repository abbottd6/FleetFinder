package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.ExperienceRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameExperienceDto;
import com.sc_fleetfinder.fleets.entities.GameExperience;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GameExperienceConversionServiceImpl implements GameExperienceConversionService {

    private static final Logger log = LoggerFactory.getLogger(GameExperienceConversionServiceImpl.class);
    private final ExperienceRepository experienceRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public GameExperienceConversionServiceImpl(ExperienceRepository experienceRepository) {
        this.experienceRepository = experienceRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public GameExperienceDto convertToDto(GameExperience entity) {
        //id valid check
        if(entity.getExperienceId() == null || entity.getExperienceId() == 0) {
            log.error("Experience convertToDto encountered an id that is null or 0.");
            throw new IllegalArgumentException("Experience id is null or 0");
        }

        //type/name valid check
        if(entity.getExperienceType() == null || entity.getExperienceType().isEmpty()) {
            log.error("Experience convertToDto encountered an type that is null or empty.");
            throw new IllegalArgumentException("Experience type is null or empty");
        }

        return modelMapper.map(entity, GameExperienceDto.class);
    }

    @Override
    public GameExperience convertToEntity(GameExperienceDto dto) {
        //checking repository for entity matching Dto id
        GameExperience entity = experienceRepository.findById(dto.getExperienceId())
                .orElseThrow(() -> {
                    log.error("GameExperience convertToEntity could not find Experience with Id: {}",
                            dto.getExperienceId());
                    return new ResourceNotFoundException("Experience with ID: " + dto.getExperienceId()
                            + " not found");
                });
        //verifying name/id match for dto and entity
        if(!Objects.equals(dto.getExperienceType(), entity.getExperienceType())) {
            log.error("GameExperience convertToEntity encountered an id/name mismatch for Id: {}, and experience: {}",
                    dto.getExperienceId(), dto.getExperienceType());
            throw new ResourceNotFoundException("Experience type mismatch for Dto: " + dto.getExperienceType()
                    + ", ID: " + dto.getExperienceId() + " and entity: " + entity.getExperienceType()
                    + ", ID: " + entity.getExperienceId());
        }
        //if Id exists and names match then returns entity
        return entity;
    }
}
