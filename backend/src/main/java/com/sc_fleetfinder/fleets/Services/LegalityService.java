package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.entities.Legality;

import java.util.List;

public interface LegalityService {

    List<Legality> getAllLegalities();
    Legality getLegalityById(int id);
}
