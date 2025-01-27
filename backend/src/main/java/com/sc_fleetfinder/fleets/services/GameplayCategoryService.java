package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;

import java.util.List;

public interface GameplayCategoryService {

    List<GameplayCategoryDto> getAllCategories();
    GameplayCategoryDto getCategoryById(Integer id);
}
