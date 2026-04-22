package com.sc_fleetfinder.fleets.events.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupInvite;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.Users;

public record NewGroupMemberNotifyEvent(Users notificationRecipient,
                                        GroupInvite invite,
                                        GroupMember newMember) {
}
