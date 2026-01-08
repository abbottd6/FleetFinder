package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SearchListingsDto {
    private String search;
    private Integer server;
    private Integer environment;
    private Integer experience;
    private Integer playStyle;
    private Integer category;
    private Integer subcategory;
    private Integer legality;
    private Integer pvpStatus;
    private Integer system;
    private Integer planetMoonSystem;
    private Integer groupStatus;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private Integer commsOption;
    private int page;
    private int size;
    private String sortField;
    private String sortDirection;
}
