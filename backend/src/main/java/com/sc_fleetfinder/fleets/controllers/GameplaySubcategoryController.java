package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.services.CRUD_services.GameplaySubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lookup/gameplay-subcategories")
public class GameplaySubcategoryController {

    @Autowired
    private GameplaySubcategoryService gameplaySubcategoryService;

    @GetMapping
    public List<GameplaySubcategoryDto> getAllGameplaySubcategories() {
        return gameplaySubcategoryService.getAllSubcategories();
    }

    @GetMapping("/{id}")
    public GameplaySubcategoryDto getGameplaySubcategoryById(@PathVariable Integer id) {
        return gameplaySubcategoryService.getSubcategoryById(id);
    }
}
