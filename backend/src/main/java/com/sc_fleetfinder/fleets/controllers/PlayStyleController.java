package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.PlayStyleDto;
import com.sc_fleetfinder.fleets.entities.PlayStyle;
import com.sc_fleetfinder.fleets.services.PlayStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/playStyles")
public class PlayStyleController {

    @Autowired
    private PlayStyleService playStyleService;

    @GetMapping
    public List<PlayStyleDto> getAllPlayStyles() {
        return playStyleService.getAllPlayStyles();
    }

    @GetMapping("/{id}")
    public PlayStyleDto getPlayStyleById(@RequestParam int id) {
        return playStyleService.getPlayStyleById(id);
    }
}
