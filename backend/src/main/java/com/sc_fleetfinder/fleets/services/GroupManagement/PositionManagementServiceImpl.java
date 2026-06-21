package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewPositionRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionCrewPositionDto;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPosition;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PositionManagementServiceImpl implements PositionManagementService {

    private final CrewPositionRepository cpr;

    @Override
    public List<CrewPosition> findAllPositionsByGroupId_IncludeDeleted(Long groupId) {
        return cpr.findAllPositionsByGroupId_IncludeDeleted(groupId);
    }

    @Override
    public Optional<CrewPosition> findMemberPositionByAssignedMemberIdAndLListingId(GroupMember member, Long groupId) {
        return cpr.findMemberPositionByAssignedMemberIdAndListingId(member, groupId);
    }

    @Override
    public List<CrewPosition> findAllPositionsForSubgroupTrees(List<Long> rootIds, Long groupId) {
        return cpr.findAllPositionsForSubgroupTrees_ExcludeDeleted(rootIds, groupId);
    }

    @Override
    public Optional<CrewPosition> findById(Long positionId) {
        return cpr.findById(positionId);
    }

    @Override
    @Transactional
    public void softDeletePosition(Long positionId) {
        cpr.findById(positionId).ifPresent(pos -> {
            pos.setDeletedAt(Instant.now());
            pos.setAssignedMemberUserId(null);
        });
    }

    @Override
    @Transactional
    public void softDeleteAllBySubgroup(Long subgroupId) {
        cpr.softDeleteAllBySubgroup(subgroupId);
    }

    @Override
    @Transactional
    public void restoreSoftDeletedPosition(Long positionId, GroupCompositionCrewPositionDto posDto) {
        Long requestedMemberId = posDto.getAssignedMember().getUserSummary().getUserId();

        // if the member has been reassigned elsewhere, do not restore the assignment to this restored position
        boolean assignedElsewhere = requestedMemberId != null &&
                cpr.findPositionByAssignedMemberUserIdAndListingId(requestedMemberId, posDto.getGroupId()).isPresent();

        Long assignedMemberId = assignedElsewhere ? null : requestedMemberId;

        cpr.findById(positionId).ifPresent(pos -> {
            pos.setDeletedAt(null);
            pos.setAssignedMemberUserId(assignedMemberId);
        });
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
