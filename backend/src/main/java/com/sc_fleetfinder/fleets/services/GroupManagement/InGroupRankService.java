package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.InGroupRank;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupRankGenericTypes;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InGroupRankService {
    InGroupRank getGenericRankByTitle(GroupRankGenericTypes title);

    Boolean verifyUserRankPermissions(Users user, GroupListing listing, RankPrivilegeOptions action);

    Page<GroupListing> getMyInviteAuthorizedGroups(Long userId, Pageable pageable);
}
