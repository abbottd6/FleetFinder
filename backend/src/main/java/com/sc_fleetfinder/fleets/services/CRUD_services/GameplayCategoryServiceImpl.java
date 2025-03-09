package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.caching_services.CategoryCachingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameplayCategoryServiceImpl implements GameplayCategoryService {

    private static final Logger log = LoggerFactory.getLogger(GameplayCategoryServiceImpl.class);
    private final CategoryCachingServiceImpl categoryCachingService;

    public GameplayCategoryServiceImpl(GameplayCategoryRepository gameplayCategoryRepository,
                                       CategoryCachingServiceImpl categoryCachingService) {
        this.categoryCachingService = categoryCachingService;
    }

    @Override
    public List<GameplayCategoryDto> getAllCategories() {
        return categoryCachingService.cacheAllCategories();
    }

    @Override
    public GameplayCategoryDto getCategoryById(Integer id) {
        List<GameplayCategoryDto> cachedCategories = categoryCachingService.cacheAllCategories();

        return cachedCategories.stream()
                .filter(category -> category.getGameplayCategoryId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Category with id {} not found", id);
                    return new ResourceNotFoundException(id);
                });
    }


}
