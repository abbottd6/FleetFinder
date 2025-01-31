package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;

import java.util.List;

public interface GameplayCategoryService {

    List<GameplayCategoryDto> getAllCategories();
    GameplayCategoryDto getCategoryById(Integer id);
    GameplayCategoryDto convertToDto(GameplayCategory gameplayCategory);
}
