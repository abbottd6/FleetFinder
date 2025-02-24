package com.sc_fleetfinder.fleets.services.conversion_services;

import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GameplayCategoryConversionServiceImpl implements GameplayCategoryConversionService {

    private static final Logger log = LoggerFactory.getLogger(GameplayCategoryConversionServiceImpl.class);
    private final GameplayCategoryRepository gameplayCategoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public GameplayCategoryConversionServiceImpl(GameplayCategoryRepository gameplayCategoryRepository) {
        this.gameplayCategoryRepository = gameplayCategoryRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public GameplayCategoryDto convertToDto(GameplayCategory entity) {

        if(entity.getCategoryId() == null || entity.getCategoryId() == 0) {
            throw new ResourceNotFoundException("Category id is null or empty");
        }

        if(entity.getCategoryName() == null || entity.getCategoryName().isEmpty()) {
            throw new ResourceNotFoundException("Category name is null or empty");
        }

        return modelMapper.map(entity, GameplayCategoryDto.class);
    }

    @Override
    public GameplayCategory convertToEntity(GameplayCategoryDto gameplayCategoryDto) {

        //checking for Dto id match in repository
        GameplayCategory gameplayCategory = gameplayCategoryRepository.findById(gameplayCategoryDto.getGameplayCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Game Category with ID: " +
                        gameplayCategoryDto.getGameplayCategoryId() + " not found"));
        //Verifying that the name for the Dto matches the name of the entity with that id
        if(!Objects.equals(gameplayCategoryDto.getGameplayCategoryName(), gameplayCategory.getCategoryName())) {
            throw new ResourceNotFoundException("Gameplay Category name mismatch for DTO with ID: " +
                    gameplayCategoryDto.getGameplayCategoryId() + " and Category name: "
                    + gameplayCategoryDto.getGameplayCategoryName());
        }
        //if Id exists and names match then returns entity
        return gameplayCategory;
    }
}
