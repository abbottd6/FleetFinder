package com.sc_fleetfinder.fleets.DTO;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sc_fleetfinder.fleets.entities.GameplaySubcategory;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class GameplayCategoryDto {

    private int gameplayCategoryId;
    private String gameplayCategoryName;
    private Set<GameplaySubcategoryDto> gameplaySubcategories = new HashSet<>();
}
