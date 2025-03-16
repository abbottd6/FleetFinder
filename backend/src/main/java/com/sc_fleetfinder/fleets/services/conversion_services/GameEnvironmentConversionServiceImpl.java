package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.EnvironmentRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameEnvironmentDto;
import com.sc_fleetfinder.fleets.entities.GameEnvironment;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GameEnvironmentConversionServiceImpl implements GameEnvironmentConversionService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentConversionServiceImpl.class);
    private final EnvironmentRepository environmentRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public GameEnvironmentConversionServiceImpl(EnvironmentRepository environmentRepository) {
        this.environmentRepository = environmentRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public GameEnvironmentDto convertToDto(GameEnvironment entity) {

        if(entity.getEnvironmentId() == null || entity.getEnvironmentId() == 0) {
            log.error("Environment convertToDto encountered a null or zero Id value.");
            throw new IllegalArgumentException("Environment id is null or zero");
        }

        if(entity.getEnvironmentType() == null || entity.getEnvironmentType().isEmpty()) {
            log.error("Environment convertToDto environment type is null or empty.");
            throw new IllegalArgumentException("Environment type is null or empty");
        }

        return modelMapper.map(entity, GameEnvironmentDto.class);
    }

    @Override
    public GameEnvironment convertToEntity(GameEnvironmentDto dto) {

        //checking repository for entity matching Dto id
        GameEnvironment entity = environmentRepository.findById(dto.getEnvironmentId())
                .orElseThrow(() -> {
                    log.error("Environment convertToEntity could not find env with Id: {}", dto.getEnvironmentId());
                    return new ResourceNotFoundException("Environment with ID: " + dto.getEnvironmentId()
                            + " not found");
                });
        //verifying name/id match for dto and entity
        if(!Objects.equals(dto.getEnvironmentType(), entity.getEnvironmentType())) {
            log.error("Environment convertToEntity encountered a id/name mismatch with Id: {}, " +
                    "and environment type: {}", dto.getEnvironmentId(),  dto.getEnvironmentType());
            throw new ResourceNotFoundException("Environment type mismatch for Dto: "
                    + dto.getEnvironmentType() + ", ID: " + dto.getEnvironmentId() + " and entity: "
                    + entity.getEnvironmentId() + ", ID: " + entity.getEnvironmentType());
        }
        //if Id exists and names match then returns entity
        return entity;
    }
}
