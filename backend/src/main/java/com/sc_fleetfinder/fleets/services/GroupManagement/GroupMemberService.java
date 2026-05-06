package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPosition;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupInvite;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import com.sc_fleetfinder.fleets.entities.GroupManagement.InGroupRank;
import com.sc_fleetfinder.fleets.entities.Users;

import java.util.Optional;

public interface GroupMemberService {

    void throwIfUserIsAlreadyAMember(Users user, Long listingId);

    void userDismissInvite(Users user, Long inviteId);

    Boolean verifyUserIsAuthorizedManager(Users user, Long listingId);

    GroupMember verifyAndReturnUserAsGroupMember(Users user, Long listingId);

    Optional<CrewPosition> findGroupMemberCrewPosition(GroupMember member);

    boolean hasGroupManagementPrivileges(InGroupRank rank);

    Boolean getNewMemberHasExternalNotes(Users newMember);

    void createOwnerMember(Users user, GroupListing listing);

    void evaluateForInviteStatusConflict(GroupInvite invite);
}
