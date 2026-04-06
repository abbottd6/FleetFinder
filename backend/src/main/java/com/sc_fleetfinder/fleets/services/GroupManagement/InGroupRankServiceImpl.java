package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.InGroupRankRepository;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.InGroupRank;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ActionNotAuthorizedException;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupRankGenericTypes;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InGroupRankServiceImpl implements InGroupRankService {

    private final InGroupRankRepository rankRepo;
    private Map<GroupRankGenericTypes, InGroupRank> genericRanksCache;

    @Override
    public Boolean verifyUserRankPermissions(Users user, GroupListing listing, RankPrivilegeOptions action) {
        Set<RankPrivilegeOptions> memberPrivileges = rankRepo.findPrivilegesByUserIdAndListingId(
                user.getUserId(), listing.getGroupId()
        );

        if(memberPrivileges.contains(action)) {
            return true;
        } else {
            log.warn("UserId [{}] attempted to perform a Group Management action: {} on a listingId: {}" +
                    " where they do not have this permission.", user.getUserId(), action, listing.getGroupId());
            throw new ActionNotAuthorizedException(user.getUserId(),
                    action.toString(), "Group Management", listing.getGroupId());
        }
    }

    @PostConstruct
    private void cacheGenerics() {
        genericRanksCache = rankRepo.findAllGenericRanks().stream()
                .collect(Collectors.toMap(
                        r -> GroupRankGenericTypes.valueOf(r.getRankTitle()),
                        r -> r
                ));
    }

    @Override
    public InGroupRank getGenericRankByTitle(GroupRankGenericTypes title) {
        return genericRanksCache.get(title);
    }
}
