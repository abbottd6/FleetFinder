package com.sc_fleetfinder.fleets.events;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;

public record ListingModDeleteEvent(GroupListing listing, Users owner) {
}
