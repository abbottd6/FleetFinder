package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import lombok.Data;

import java.util.List;

@Data
public class SearchListingsDto {
    private String search;
    private List<String> filters;
    private int page;
    private int size;
}
