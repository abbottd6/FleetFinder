package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.PvpStatusDto;
import com.sc_fleetfinder.fleets.services.CRUD_services.PvpStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lookup/pvp-statuses")
public class PvpStatusController {

    @Autowired
    private PvpStatusService pvpStatusService;

    @GetMapping
    public List<PvpStatusDto> getAllPvpStatuses(){ return pvpStatusService.getAllPvpStatuses(); }

    @GetMapping("/{id}")
    public PvpStatusDto getPvpStatusById(@PathVariable Integer id) { return pvpStatusService.getPvpStatusById(id); }
}
