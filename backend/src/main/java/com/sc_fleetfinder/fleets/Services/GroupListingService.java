package com.sc_fleetfinder.fleets.Services;

import com.sc_fleetfinder.fleets.entities.GroupListing;

import java.util.List;

public interface GroupListingService {

    List<GroupListing> getAllGroupListings();
    GroupListing createGroupListing(GroupListing groupListing);
    GroupListing updateGroupListing(Long id, GroupListing groupListing);
    void deleteGroupListing(GroupListing groupListing);
    GroupListing getGroupListingById(Long id);
}
