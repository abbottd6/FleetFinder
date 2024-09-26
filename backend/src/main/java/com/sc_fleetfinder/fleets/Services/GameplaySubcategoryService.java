package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;

import java.util.List;

public interface GameplaySubcategoryService {

    List<GameplaySubcategory> getAllSubcategories();
    GameplaySubcategory getSubcategoryById(int id);
}
