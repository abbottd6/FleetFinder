package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;

import java.util.List;

public interface GameplayCategoryService {

    List<GameplayCategoryDto> getAllCategories();
    GameplayCategoryDto getCategoryById(int id);
}
