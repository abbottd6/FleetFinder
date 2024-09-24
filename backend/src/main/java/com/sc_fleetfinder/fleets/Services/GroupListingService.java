package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.DTO.GroupListingDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;

import java.util.List;

public interface GroupListingService {

    List<GroupListingDto> getAllGroupListings();
    GroupListing createGroupListing(GroupListingDto groupListingDto);
    GroupListing updateGroupListing(Long id, GroupListingDto groupListingDto);
    void deleteGroupListing(GroupListing groupListing);
    GroupListingDto getGroupListingById(Long id);
}
