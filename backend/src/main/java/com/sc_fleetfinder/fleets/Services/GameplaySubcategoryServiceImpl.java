package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameplaySubcategoryServiceImpl implements GameplaySubcategoryService {

    private final GameplaySubcategoryRepository gameplaySubcategoryRepository;

    public GameplaySubcategoryServiceImpl(GameplaySubcategoryRepository gameplaySubcategoryRepository) {
        super();
        this.gameplaySubcategoryRepository = gameplaySubcategoryRepository;
    }

    @Override
    public List<GameplaySubcategory> getAllSubcategories() {
        return gameplaySubcategoryRepository.findAll();
    }

    @Override
    public GameplaySubcategory getSubcategoryById(int id) {
        Optional<GameplaySubcategory> gameplaySubcategory = gameplaySubcategoryRepository.findById(id);
        if (gameplaySubcategory.isPresent()) {
            return gameplaySubcategory.get();
        }
        else {
            throw new ResourceNotFoundException("Game play subcategory with id " + id + " not found");
        }
    }
}
