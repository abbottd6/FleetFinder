package com.sc_fleetfinder.fleets.DTO;

import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import lombok.Data;

@Data
public class GameplaySubcategoryDto {

    private int subcategoryId;
    private String subcategoryName;
    private GameplayCategory gameplayCategory;
}
