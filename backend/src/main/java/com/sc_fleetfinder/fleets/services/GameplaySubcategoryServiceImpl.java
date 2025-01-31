package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.SubcategoryCachingService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GameplaySubcategoryServiceImpl implements GameplaySubcategoryService {

    private static final Logger log = LoggerFactory.getLogger(GameEnvironmentServiceImpl.class);
    private final GameplaySubcategoryRepository gameplaySubcategoryRepository;
    private final SubcategoryCachingService subcategoryCachingService;
    private final ModelMapper modelMapper;

    public GameplaySubcategoryServiceImpl(GameplaySubcategoryRepository gameplaySubcategoryRepository,
                                          SubcategoryCachingService subcategoryCachingService) {
        super();
        this.subcategoryCachingService = subcategoryCachingService;
        this.gameplaySubcategoryRepository = gameplaySubcategoryRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public List<GameplaySubcategoryDto> getAllSubcategories() {
        return subcategoryCachingService.cacheAllSubcategories();
    }

    @Override
    public GameplaySubcategoryDto getSubcategoryById(Integer id) {
        List<GameplaySubcategoryDto> cachedSubcategories = subcategoryCachingService.cacheAllSubcategories();

        return cachedSubcategories.stream()
                .filter(subcategory -> subcategory.getSubcategoryId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(id));
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
