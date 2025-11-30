package com.sc_fleetfinder.fleets.controllers.ListingReferenceDataControllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReferenceDataDTOs.PlayStyleDto;
import com.sc_fleetfinder.fleets.services.CRUD_services.ListingReferenceDataCRUD.PlayStyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lookup/play-styles")
public class PlayStyleController {

    @Autowired
    private PlayStyleService playStyleService;

    @GetMapping
    public List<PlayStyleDto> getAllPlayStyles() {
        return playStyleService.getAllPlayStyles();
    }

    @GetMapping("/{id}")
    public PlayStyleDto getPlayStyleById(@PathVariable Integer id) {
        return playStyleService.getPlayStyleById(id);
    }
}
