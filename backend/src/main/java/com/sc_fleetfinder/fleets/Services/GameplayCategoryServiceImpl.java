package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DAO.GameplayCategoryRepository;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameplayCategoryServiceImpl implements GameplayCategoryService {

    private final GameplayCategoryRepository gameplayCategoryRepository;

    public GameplayCategoryServiceImpl(GameplayCategoryRepository gameplayCategoryRepository) {
        super();
        this.gameplayCategoryRepository = gameplayCategoryRepository;
    }

    @Override
    public List<GameplayCategory> getAllCategories() {
        return gameplayCategoryRepository.findAll();
    }

    @Override
    public GameplayCategory getCategoryById(int id) {
        Optional<GameplayCategory> gameplayCategory = gameplayCategoryRepository.findById(id);
        if (gameplayCategory.isPresent()) {
            return gameplayCategory.get();
        }
        else {
            throw new ResourceNotFoundException("Game Category with id " + id + " not found");
        }
    }
}
