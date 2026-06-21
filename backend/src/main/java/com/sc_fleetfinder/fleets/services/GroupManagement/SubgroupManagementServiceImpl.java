package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.GroupManagementSubgroupRepository;
import com.sc_fleetfinder.fleets.entities.GroupManagement.GroupManagementSubgroup;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.sc_fleetfinder.fleets.entities.GroupManagement.GroupManagementConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubgroupManagementServiceImpl implements SubgroupManagementService {

    private final GroupManagementSubgroupRepository gmsr;

    @Override
    @Transactional(readOnly = true)
    public GroupManagementSubgroup findSubgroupById(Long subgroupId) {
        return gmsr.findById(subgroupId)
                .orElseThrow(() -> new ResourceNotFoundException("GroupManagementSubgroup", subgroupId));
    }

    @Override
    @Transactional(readOnly = true)
    public HashMap<Long, List<GroupManagementSubgroup>> findTreeMapByRootId(List<Long> rootIds, Long groupId) {
        return gmsr.findTreeByRoot_ExcludeDeleted(rootIds, groupId)
                .stream()
                .collect(Collectors.groupingBy(sub ->
                        sub.getParentSubgroup() == null ? ROOT_SUBGROUP_ID : sub.getParentSubgroup().getSubgroupId(),
                        HashMap::new,
                        Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findGroupCompositionRootIds(Long groupId) {
        return gmsr.findGroupCompositionRootIds_ExcludeDeleted(groupId);
    }

    @Override
    public List<GroupManagementSubgroup> findSubgroupsByGroupId_IncludeDeleted(Long groupId) {
        return gmsr.findSubgroupsByGroupListingId_IncludeDeleted(groupId);
    }

    @Override
    @Transactional
    public GroupManagementSubgroup saveSubgroup(GroupManagementSubgroup subgroup) {
        return gmsr.save(subgroup);
    }

    @Override
    @Transactional
    public List<GroupManagementSubgroup> saveListOf(List<GroupManagementSubgroup> subgroups) {
        return gmsr.saveAll(subgroups);
    }

    @Override
    @Transactional
    public void softDeleteSubgroup(Long subgroupId) {
        gmsr.findById(subgroupId).ifPresent(sub -> sub.setDeletedAt(Instant.now()));
    }

    @Override
    @Transactional
    public void restoreSoftDeletedSubgroup(Long subgroupId) {
        gmsr.findById(subgroupId).ifPresent(subgroup -> subgroup.setDeletedAt(null));
    }

    @Override
    @Transactional
    public GroupManagementSubgroup updateSubgroupLabel(GroupManagementSubgroup subgroup, String newLabel) {
        subgroup.setSubgroupLabel(newLabel);
        return gmsr.save(subgroup);
    }
}
