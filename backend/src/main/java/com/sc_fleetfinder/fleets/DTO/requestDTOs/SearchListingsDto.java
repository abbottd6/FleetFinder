package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    private Date eventSchedule;
    private Integer commsOption;
    private int page;
    private int size;


    public Map<String, Integer> getFilters() {
//        Map<String, Integer> filters = new HashMap<>();
        return new HashMap<>();
//        filters.put(
    }
}
