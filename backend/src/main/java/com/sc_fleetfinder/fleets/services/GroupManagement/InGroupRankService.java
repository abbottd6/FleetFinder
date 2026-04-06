package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.InGroupRank;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupRankGenericTypes;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;

public interface InGroupRankService {
    InGroupRank getGenericRankByTitle(GroupRankGenericTypes title);

    Boolean verifyUserRankPermissions(Users user, GroupListing listing, RankPrivilegeOptions action);
}
