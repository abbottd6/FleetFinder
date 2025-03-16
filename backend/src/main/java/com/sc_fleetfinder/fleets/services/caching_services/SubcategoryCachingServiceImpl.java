package com.sc_fleetfinder.fleets.services.caching_services;

import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GameplaySubcategoryService;
import com.sc_fleetfinder.fleets.services.conversion_services.GameplaySubcategoryConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubcategoryCachingServiceImpl implements SubcategoryCachingService {

    private static final Logger log = LoggerFactory.getLogger(SubcategoryCachingServiceImpl.class);
    private final GameplaySubcategoryRepository gameplaySubcategoryRepository;
    private final GameplaySubcategoryConversionService gameplaySubcategoryConversionService;

    @Autowired
    public SubcategoryCachingServiceImpl(GameplaySubcategoryRepository gameplaySubcategoryRepository,
                                     GameplaySubcategoryConversionService gameplaySubcategoryConversionService) {

        this.gameplaySubcategoryRepository = gameplaySubcategoryRepository;
        this.gameplaySubcategoryConversionService = gameplaySubcategoryConversionService;
    }

    @Override
    @Cacheable(value = "subcategoryCache", key = "'allSubcategoriesCache'")
    public List<GameplaySubcategoryDto> cacheAllSubcategories() {
        List<GameplaySubcategory> subcategories = gameplaySubcategoryRepository.findAll();

        if(subcategories.isEmpty()) {
            log.error("Unable to access Subcategory data for caching.");
            throw new ResourceNotFoundException("Unable to access data for gameplay subcategories");
        }
        return subcategories.stream()
                .map(gameplaySubcategoryConversionService::convertToDto)
                .collect(Collectors.toList());
    }

}
