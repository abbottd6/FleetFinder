package com.sc_fleetfinder.fleets.DTO.requestDTOs;

import lombok.Data;

@Data
public class GenericPageRequestDto {
    private int pageIdx;
    private int pageSize;
}
