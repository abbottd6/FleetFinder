package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;

import java.util.List;

public interface GameplaySubcategoryService {

    List<GameplaySubcategoryDto> getAllSubcategories();
    GameplaySubcategoryDto getSubcategoryById(int id);
}
