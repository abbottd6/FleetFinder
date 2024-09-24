package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.entities.PlayStyle;

import java.util.List;

public interface PlayStyleService {

    List<PlayStyle> getAllPlayStyles();
    PlayStyle getPlayStyleById(int id);
}
