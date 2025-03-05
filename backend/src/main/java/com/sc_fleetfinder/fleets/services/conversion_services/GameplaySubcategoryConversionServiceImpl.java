package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GameplaySubcategoryConversionServiceImpl implements GameplaySubcategoryConversionService {

    private static final Logger log = LoggerFactory.getLogger(GameplaySubcategoryConversionServiceImpl.class);
    private final GameplaySubcategoryRepository gameplaySubcategoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public GameplaySubcategoryConversionServiceImpl(GameplaySubcategoryRepository gameplaySubcategoryRepository) {
        this.gameplaySubcategoryRepository = gameplaySubcategoryRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public GameplaySubcategoryDto convertToDto(GameplaySubcategory entity) {
        //id valid check
        if(entity.getSubcategoryId() == null || entity.getSubcategoryId() == 0) {
            log.error("Subcategory convertToDto encountered an entity with an id that is null or 0.");
            throw new IllegalArgumentException("Subcategory id is null or empty");
        }

        //type/name valid check
        if(entity.getSubcategoryName() == null || entity.getSubcategoryName().isEmpty()) {
            log.error("Subcategory convertToDto encountered an entity with an name that is null or empty.");
            throw new IllegalArgumentException("Subcategory name is null or empty");
        }
        //mapping to Dto if entity is valid
        return modelMapper.map(entity, GameplaySubcategoryDto.class);
    }

    @Override
    public GameplaySubcategory convertToEntity(GameplaySubcategoryDto dto) {
        //checking for Dto id match in repository
        GameplaySubcategory gameplaySubcategory = gameplaySubcategoryRepository.findById(dto.getSubcategoryId())
                .orElseGet(() -> {
                    log.error("Subcategory convertToEntity could not find entity with Id: {}",
                            dto.getSubcategoryId());
                    throw new ResourceNotFoundException("Subcategory with ID: " +
                            dto.getSubcategoryId() + " not found");
                });

        //Verifying that the dto name matches the name of the entity with that Id
        if(!Objects.equals(dto.getSubcategoryName(), gameplaySubcategory.getSubcategoryName())) {
            log.error("Subcategory convertToEntity encountered a id/name mismatch for Id: {}, and subcategory: {}",
                    dto.getSubcategoryId(), dto.getSubcategoryName());
            throw new ResourceNotFoundException("Gameplay subcategory name mismatch for DTO with ID: " +
                    dto.getSubcategoryId() + " and name: "
                    + dto.getSubcategoryName());
        }
        //if Id exists in repo and name matches then return entity
        return gameplaySubcategory;
    }
}
