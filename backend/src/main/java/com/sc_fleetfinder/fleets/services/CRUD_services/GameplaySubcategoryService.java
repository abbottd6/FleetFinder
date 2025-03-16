package com.sc_fleetfinder.fleets.services.CRUD_services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;

import java.util.List;

public interface GameplaySubcategoryService {

    List<GameplaySubcategoryDto> getAllSubcategories();
    GameplaySubcategoryDto getSubcategoryById(Integer id);
}
