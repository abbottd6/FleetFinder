package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.GameplaySubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubcategoryCachingServiceImpl implements SubcategoryCachingService {

    private final GameplaySubcategoryRepository gameplaySubcategoryRepository;
    private final GameplaySubcategoryService gameplaySubcategoryService;

    @Autowired
    public SubcategoryCachingServiceImpl(GameplaySubcategoryRepository gameplaySubcategoryRepository,
                                     GameplaySubcategoryService gameplaySubcategoryService) {

        this.gameplaySubcategoryRepository = gameplaySubcategoryRepository;
        this.gameplaySubcategoryService = gameplaySubcategoryService;
    }

    @Override
    @Cacheable(value = "subcategoryCache", key = "'allSubcategoriesCache'")
    public List<GameplaySubcategoryDto> cacheAllSubcategories() {
        List<GameplaySubcategory> subcategories = gameplaySubcategoryRepository.findAll();

        if(subcategories.isEmpty()) {
            throw new ResourceNotFoundException("Unable to access data for gameplay subcategories");
        }
        return subcategories.stream()
                .map(gameplaySubcategoryService::convertToDto)
                .collect(Collectors.toList());
    }

}
