package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.conversion_services.GameplayCategoryConversionService;
import com.sc_fleetfinder.fleets.services.conversion_services.GameplayCategoryConversionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryCachingServiceImpl implements CategoryCachingService {

    private final GameplayCategoryRepository categoryRepository;
    private final GameplayCategoryConversionService gameplayCategoryConversionService;

    @Autowired
    public CategoryCachingServiceImpl(GameplayCategoryRepository categoryRepository,
                                      GameplayCategoryConversionServiceImpl categoryConversionService) {
        this.categoryRepository = categoryRepository;
        this.gameplayCategoryConversionService = categoryConversionService;
    }

    @Override
    @Cacheable(value = "categoryCache", key= "'allCategoriesCache'")
    public List<GameplayCategoryDto> cacheAllCategories() {
        List<GameplayCategory> categories = categoryRepository.findAll();

        if(categories.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for gameplay categories");
        }

        return categories.stream()
                .map(gameplayCategoryConversionService::convertToDto)
                .collect(Collectors.toList());
    }
}
