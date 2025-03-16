package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ServerRegionDto;
import com.sc_fleetfinder.fleets.services.CRUD_services.ServerRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lookup/server-regions")
public class ServerRegionController {

    @Autowired
    private ServerRegionService serverRegionService;

    @GetMapping
    public List<ServerRegionDto> getAllServerRegions() {
        return serverRegionService.getAllServerRegions();
    }

    @GetMapping("/{id}")
    public ServerRegionDto getServerRegionById(@PathVariable Integer id) {
        return serverRegionService.getServerRegionById(id);
    }
}
