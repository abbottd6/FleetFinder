package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewRoleClassificationRepository;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewRoleClassification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrewRoleServiceImpl implements CrewRoleService {

    private final CrewRoleClassificationRepository crcr;

    @Override
    public List<CrewRoleClassification> getRolesForListingByUserId(Long userId) {
        return crcr.findByUserAndGlobalClassifications(userId);
    }

    @Override
    public CrewRoleClassification findByRoleId(Long roleId) {
        return crcr.findById(roleId).orElse(null);
    }

    @Override
    public Map<Long, CrewRoleClassification> constructCrewRolesMapForGroup(Long listingOwnerUserId) {
        // find roles available within this listing scope for position assignments because the position dto uses a dto
        // (can't assign the role classification from the dto to the position entity's role)
        List<CrewRoleClassification> listingScopedRoles = getRolesForListingByUserId(listingOwnerUserId);

        // convert to map for faster lookup
        return listingScopedRoles.stream()
                .collect(Collectors.toMap(CrewRoleClassification::getRoleId, Function.identity()));
    }
}
