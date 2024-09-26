package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DAO.GameplaySubcategoryRepository;
import com.sc_fleetfinder.fleets.DTO.GameplaySubcategoryDto;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import org.modelmapper.ModelMapper;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameplaySubcategoryServiceImpl implements GameplaySubcategoryService {

    private final GameplaySubcategoryRepository gameplaySubcategoryRepository;
    private final ModelMapper modelMapper;

    public GameplaySubcategoryServiceImpl(GameplaySubcategoryRepository gameplaySubcategoryRepository) {
        super();
        this.gameplaySubcategoryRepository = gameplaySubcategoryRepository;
        this.modelMapper = new ModelMapper();
    }

    @Override
    public List<GameplaySubcategoryDto> getAllSubcategories() {
        List<GameplaySubcategory> subcategories = gameplaySubcategoryRepository.findAll();
        return subcategories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GameplaySubcategoryDto getSubcategoryById(int id) {
        Optional<GameplaySubcategory> gameplaySubcategory = gameplaySubcategoryRepository.findById(id);
        if (gameplaySubcategory.isPresent()) {
            return convertToDto(gameplaySubcategory.get());
        }
        else {
            throw new ResourceNotFoundException("Game play subcategory with id " + id + " not found");
        }
    }

    public GameplaySubcategoryDto convertToDto(GameplaySubcategory gameplaySubcategory) {
        return modelMapper.map(gameplaySubcategory, GameplaySubcategoryDto.class);
    }

    public GameplaySubcategory convertToEntity(GameplaySubcategoryDto gameplaySubcategoryDto) {
        return modelMapper.map(gameplaySubcategoryDto, GameplaySubcategory.class);
    }

}
