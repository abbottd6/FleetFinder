package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;

import java.util.List;

public interface GameplaySubcategoryService {

    List<GameplaySubcategoryDto> getAllSubcategories();
    GameplaySubcategoryDto getSubcategoryById(Integer id);
    GameplaySubcategoryDto convertToDto(GameplaySubcategory entity);
}
