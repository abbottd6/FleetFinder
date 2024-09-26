package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.entities.GameplayCategory;

import java.util.List;

public interface GameplayCategoryService {

    List<GameplayCategory> getAllCategories();
    GameplayCategory getCategoryById(int id);
}
