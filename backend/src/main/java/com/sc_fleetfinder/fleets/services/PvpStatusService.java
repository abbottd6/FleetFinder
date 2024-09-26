package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.PvpStatusDto;

import java.util.List;

public interface PvpStatusService {

    List<PvpStatusDto> getAllPvpStatuses();
    PvpStatusDto getPvpStatusById(int id);
}
