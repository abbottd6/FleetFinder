package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GameplayCategoryDto getCategoryById(int id) {
        Optional<GameplayCategory> gameplayCategory = gameplayCategoryRepository.findById(id);
        if (gameplayCategory.isPresent()) {
            return convertToDto(gameplayCategory.get());
        }
        else {
            throw new ResourceNotFoundException("Game Category with id " + id + " not found");
        }
    }

    public GameplayCategoryDto convertToDto(GameplayCategory entity) {
        return modelMapper.map(entity, GameplayCategoryDto.class);
    }

    public GameplayCategory convertToEntity(GameplayCategoryDto dto) {
        return modelMapper.map(dto, GameplayCategory.class);
    }
}
