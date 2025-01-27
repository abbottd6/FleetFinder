package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.LegalityDto;
import com.sc_fleetfinder.fleets.services.LegalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/legalities")
public class LegalityController {

    @Autowired
    private LegalityService legalityService;

    @GetMapping
    public List<LegalityDto> getAllLegalities() {
        return legalityService.getAllLegalities();
    }

    @GetMapping("/{id}")
    public LegalityDto getLegalityById(@PathVariable Integer id) {
        return legalityService.getLegalityById(id);
    }

}
