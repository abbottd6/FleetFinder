package com.sc_fleetfinder.fleets.DTO;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sc_fleetfinder.fleets.entities.GameplayCategory;
import lombok.Data;

@Data
public class GameplaySubcategoryDto {

    private int subcategoryId;
    private String subcategoryName;
    private String gameplayCategoryName;
}
