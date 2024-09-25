package com.sc_fleetfinder.fleets.controllers;


import com.sc_fleetfinder.fleets.DTO.GroupListingDto;
import com.sc_fleetfinder.fleets.Services.GroupListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/groupListings")
public class GroupListingsController {

    @Autowired
    private GroupListingService groupListingService;

    @GetMapping
    public List<GroupListingDto> getGroupListings() {
        return groupListingService.getAllGroupListings();
    }

    @GetMapping("/{id}")
    public GroupListingDto getGroupListingById(@PathVariable Long id) {
        return groupListingService.getGroupListingById(id);
    }
}
