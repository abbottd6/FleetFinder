package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;

import java.util.List;

public interface GameplaySubcategoryService {

    List<GameplaySubcategoryDto> getAllSubcategories();
    GameplaySubcategoryDto getSubcategoryById(int id);
}
