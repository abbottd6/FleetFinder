package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.SubcategoryCachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameplaySubcategoryServiceImpl implements GameplaySubcategoryService {

    private static final Logger log = LoggerFactory.getLogger(GameplaySubcategoryServiceImpl.class);
    private final SubcategoryCachingService subcategoryCachingService;

    public GameplaySubcategoryServiceImpl(SubcategoryCachingService subcategoryCachingService) {
        this.subcategoryCachingService = subcategoryCachingService;
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
                .orElseGet(() -> {
                    log.error("Subcategory with id {} not found", id);
                    throw new ResourceNotFoundException(id);
                });
    }
}
