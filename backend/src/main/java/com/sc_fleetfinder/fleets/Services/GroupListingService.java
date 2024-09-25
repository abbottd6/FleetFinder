package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.GroupListingDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.validation.Valid;

import java.util.List;

public interface GroupListingService {

    List<GroupListingDto> getAllGroupListings();
    GroupListing createGroupListing(@Valid GroupListingDto groupListingDto);
    GroupListing updateGroupListing(Long id,@Valid GroupListingDto groupListingDto);
    void deleteGroupListing(Long id);
    GroupListingDto getGroupListingById(Long id);
}
