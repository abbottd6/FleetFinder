package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import lombok.Data;

@Data
public class SortablePageRequestDto {
    private int page;
    private int size;
    private String sortField;
    private String sortDirection;
}
