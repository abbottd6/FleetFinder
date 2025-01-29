package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameplayCategoryServiceImpl implements GameplayCategoryService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final GameplayCategoryRepository gameplayCategoryRepository;
    private final ModelMapper modelMapper;

    public GameplayCategoryServiceImpl(GameplayCategoryRepository gameplayCategoryRepository) {
        super();
        this.gameplayCategoryRepository = gameplayCategoryRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    @Cacheable(value = "categoryCache", key= "'allCategoriesCache'")
    public List<GameplayCategoryDto> getAllCategories() {
        log.info("Caching test: getting all gameplay categories");
        List<GameplayCategory> categories = gameplayCategoryRepository.findAll();

        if(categories.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for gameplay categories");
        }

        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GameplayCategoryDto getCategoryById(Integer id) {
        Optional<GameplayCategory> gameplayCategory = gameplayCategoryRepository.findById(id);
        if (gameplayCategory.isPresent()) {
            return convertToDto(gameplayCategory.get());
        }
        else {
            throw new ResourceNotFoundException(id);
        }
    }

    public GameplayCategoryDto convertToDto(GameplayCategory entity) {
        return modelMapper.map(entity, GameplayCategoryDto.class);
    }

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
