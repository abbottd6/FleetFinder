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
            throw new ResourceNotFoundException("Subcategory id is null or empty");
        }

        //type/name valid check
        if(entity.getSubcategoryName() == null || entity.getSubcategoryName().isEmpty()) {
            throw new ResourceNotFoundException("Subcategory name is null or empty");
        }
        //mapping to Dto if entity is valid
        return modelMapper.map(entity, GameplaySubcategoryDto.class);
    }

    @Override
    public GameplaySubcategory convertToEntity(GameplaySubcategoryDto gameplaySubcategoryDto) {
        //checking for Dto id match in repository
        GameplaySubcategory gameplaySubcategory = gameplaySubcategoryRepository.findById(gameplaySubcategoryDto.getSubcategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory with ID: " +
                        gameplaySubcategoryDto.getSubcategoryId() + " not found"));

        //Verifying that the dto name matches the name of the entity with that Id
        if(!Objects.equals(gameplaySubcategoryDto.getSubcategoryName(), gameplaySubcategory.getSubcategoryName())) {
            throw new ResourceNotFoundException("Gameplay subcategory name mismatch for DTO with ID: " +
                    gameplaySubcategoryDto.getSubcategoryId() + " and name: "
                    + gameplaySubcategoryDto.getSubcategoryName());
        }
        //if Id exists in repo and name matches then return entity
        return gameplaySubcategory;
    }
}
