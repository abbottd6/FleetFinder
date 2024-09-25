package com.sc_fleetfinder.fleets.controllers;


import com.sc_fleetfinder.fleets.DTO.GroupListingDto;
import com.sc_fleetfinder.fleets.Services.GroupListingService;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    public GroupListing createGroupListing(@Valid @RequestBody GroupListingDto groupListingDto) {
        return groupListingService.createGroupListing(groupListingDto);
    }

    @PutMapping("/{id}")
    public GroupListing updateGroupListing(@PathVariable Long id,@Valid @RequestBody GroupListingDto groupListingDto) {
        return groupListingService.updateGroupListing(id, groupListingDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroupListing(@PathVariable Long id) {
        groupListingService.deleteGroupListing(id);
        return ResponseEntity.noContent().build();
    }
}
