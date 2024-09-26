package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.PlayStyleDto;
import com.sc_fleetfinder.fleets.entities.PlayStyle;

import java.util.List;

public interface PlayStyleService {

    List<PlayStyleDto> getAllPlayStyles();
    PlayStyleDto getPlayStyleById(int id);
}
