package com.sc_fleetfinder.fleets.events.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;

public record NewListingCreateOwnerMember(Users owner, GroupListing listing) {
}
