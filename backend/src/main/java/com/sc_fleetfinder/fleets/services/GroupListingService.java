package com.sc_fleetfinder.fleets.services;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.validation.Valid;

import java.util.List;

public interface GroupListingService {

    List<GroupListingResponseDto> getAllGroupListings();
    GroupListing createGroupListing(@Valid GroupListingResponseDto groupListingDto);
    GroupListing updateGroupListing(Long id,@Valid GroupListingResponseDto groupListingDto);
    void deleteGroupListing(Long id);
    GroupListingResponseDto getGroupListingById(Long id);
}
