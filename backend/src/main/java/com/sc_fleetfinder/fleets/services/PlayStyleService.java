package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PlayStyleDto;

import java.util.List;

public interface PlayStyleService {

    List<PlayStyleDto> getAllPlayStyles();
    PlayStyleDto getPlayStyleById(int id);
}
