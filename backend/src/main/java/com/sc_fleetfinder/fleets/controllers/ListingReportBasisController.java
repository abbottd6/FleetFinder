package com.sc_fleetfinder.fleets.controllers;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ListingReportBasisDto;
import com.sc_fleetfinder.fleets.services.reporting_services.ListingReportBasisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lookup/report-basis")
public class ListingReportBasisController {

    @Autowired
    private ListingReportBasisService lrbs;

    @GetMapping
    public List<ListingReportBasisDto> getListingReportBasis() {
        return lrbs.getAll();
    }
}
