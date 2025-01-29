package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
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
public class GameplaySubcategoryServiceImpl implements GameplaySubcategoryService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final GameplaySubcategoryRepository gameplaySubcategoryRepository;
    private final ModelMapper modelMapper;
    private final GameplayCategoryRepository gameplayCategoryRepository;

    public GameplaySubcategoryServiceImpl(GameplaySubcategoryRepository gameplaySubcategoryRepository,
                                          ModelMapper modelMapper, GameplayCategoryRepository gameplayCategoryRepository) {
        super();
        this.gameplaySubcategoryRepository = gameplaySubcategoryRepository;
        this.modelMapper = new ModelMapper();
        this.gameplayCategoryRepository = gameplayCategoryRepository;
    }

    @Override
    @Cacheable(value = "subcategoryCache", key = "'allSubcategoriesCache'")
    public List<GameplaySubcategoryDto> getAllSubcategories() {
        log.info("Caching test: getting all gameplay subcategories");
        List<GameplaySubcategory> subcategories = gameplaySubcategoryRepository.findAll();

        if(subcategories.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for gameplay subcategories");
        }
        return subcategories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GameplaySubcategoryDto getSubcategoryById(Integer id) {
        Optional<GameplaySubcategory> gameplaySubcategory = gameplaySubcategoryRepository.findById(id);
        if (gameplaySubcategory.isPresent()) {
            return convertToDto(gameplaySubcategory.get());
        }
        else {
            throw new ResourceNotFoundException(id);
        }
    }

    public GameplaySubcategoryDto convertToDto(GameplaySubcategory gameplaySubcategory) {
        return modelMapper.map(gameplaySubcategory, GameplaySubcategoryDto.class);
    }

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
