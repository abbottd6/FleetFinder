package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewPositionRepository;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPosition;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PositionManagementServiceImpl implements PositionManagementService {

    private final CrewPositionRepository cpr;

    @Override
    public List<CrewPosition> findAllPositionsByGroupId(Long groupId) {
        return cpr.findAllPositionsByGroupId(groupId);
    }

    @Override
    public Optional<CrewPosition> findMemberPositionByAssignedMemberIdAndLListingId(GroupMember member, Long groupId) {
        return cpr.findMemberPositionByAssignedMemberIdAndListingId(member, groupId);
    }

    @Override
    public List<CrewPosition> findAllPositionsForSubgroupTrees(List<Long> rootIds, Long groupId) {
        return cpr.findAllPositionsForSubgroupTrees(rootIds, groupId);
    }

    @Override
    public Optional<CrewPosition> findById(Long positionId) {
        return cpr.findById(positionId);
    }

    @Override
    @Transactional
    public CrewPosition saveAndFlush(CrewPosition position) {
        return cpr.saveAndFlush(position);
    }

    @Override
    @Transactional
    public List<CrewPosition> saveListOf(List<CrewPosition> positions) {
        return cpr.saveAll(positions);
    }
}
