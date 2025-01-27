package com.sc_fleetfinder.fleets.DTO.responseDTOs;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class GameplayCategoryDto {

    private Integer gameplayCategoryId;
    private String gameplayCategoryName;
    //decided the set of subcategories was not necessary, just using the GameplayCategory field in subcategories
    //for this information
    //private Set<GameplaySubcategoryDto> gameplaySubcategories = new HashSet<>();
}
