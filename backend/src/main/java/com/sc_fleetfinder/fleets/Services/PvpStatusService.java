package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.PvpStatusDto;
import com.sc_fleetfinder.fleets.entities.PvpStatus;

import java.util.List;

public interface PvpStatusService {

    List<PvpStatusDto> getAllPvpStatuses();
    PvpStatusDto getPvpStatusById(int id);
}
