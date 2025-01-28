package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.CreateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.UpdateGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface GroupListingService {

    List<GroupListingResponseDto> getAllGroupListings();
    @Validated
    ResponseEntity<?> createGroupListing(@Valid CreateGroupListingDto createGroupListingDto);
    @Validated
    GroupListing updateGroupListing(Long id, @Valid UpdateGroupListingDto updateGroupListingDto);
    void deleteGroupListing(Long id);
    GroupListingResponseDto getGroupListingById(Long id);
}
