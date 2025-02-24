package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplayCategoryDto;
import com.sc_fleetfinder.fleets.services.CRUD_services.GameplayCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gameplayCategories")
public class GameplayCategoryController {

    @Autowired
    private GameplayCategoryService gameplayCategoryService;

    @GetMapping
    public List<GameplayCategoryDto> getAllGameplayCategories() {
        return gameplayCategoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public GameplayCategoryDto getGameplayCategoryById(@PathVariable Integer id) {
        return gameplayCategoryService.getCategoryById(id);
    }
}
