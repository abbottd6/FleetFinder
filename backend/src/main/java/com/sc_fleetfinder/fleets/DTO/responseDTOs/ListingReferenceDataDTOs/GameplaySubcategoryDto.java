package com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs;

import lombok.Data;

@Data
public class GameplaySubcategoryDto {

    private Integer subcategoryId;
    private String subcategoryName;
    private Integer gameplayCategoryId;
    private String gameplayCategoryName;
}
