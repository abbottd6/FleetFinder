package com.sc_fleetfinder.fleets.events.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;

public record GroupMemberLeftNotifyEvent(GroupMember formerMember, GroupListing listing) {
}
