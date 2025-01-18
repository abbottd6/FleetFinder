package com.sc_fleetfinder.fleets.controllers;


import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.services.GroupListingService;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping("/api/groupListings")
public class GroupListingsController {

    @Autowired
    private GroupListingService groupListingService;

    @GetMapping
    public CollectionModel<EntityModel<GroupListingResponseDto>> getAllGroupListings() {
        List<GroupListingResponseDto> groupListingResponseDto = groupListingService.getAllGroupListings();

        List<EntityModel<GroupListingResponseDto>> groupListingModels = groupListingResponseDto.stream()
                .map(groupListing -> EntityModel.of(groupListing,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GroupListingsController.class).getAllGroupListings()).withSelfRel()))
                .toList();
        return CollectionModel.of(groupListingModels);
    }

    @GetMapping("/{id}")
    public GroupListingResponseDto getGroupListingById(@PathVariable Long id) {
        return groupListingService.getGroupListingById(id);
    }

    @PostMapping("/create_listing")
    public GroupListing createGroupListing(@Valid @RequestBody CreateGroupListingDto createGroupListingDto) {
        return groupListingService.createGroupListing(createGroupListingDto);
    }

    @PutMapping("/{id}")
    public GroupListing updateGroupListing(@PathVariable Long id, @Valid @RequestBody UpdateGroupListingDto updateGroupListingDto) {
        return groupListingService.updateGroupListing(id, updateGroupListingDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroupListing(@PathVariable Long id) {
        groupListingService.deleteGroupListing(id);
        return ResponseEntity.noContent().build();
    }
}
