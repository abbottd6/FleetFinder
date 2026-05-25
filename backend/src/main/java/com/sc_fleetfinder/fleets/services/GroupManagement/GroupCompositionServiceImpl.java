package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewPositionRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.GroupManagementSubgroupRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionCrewPositionDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionSubgroupDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.*;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupListingService;
import com.sc_fleetfinder.fleets.services.CRUD_services.UserService;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupCompositionServiceImpl implements GroupCompositionService {

    private final Long ROOT_SUBGROUP_ID = 0L;

    private final InGroupRankService rankService;
    private final GroupListingService gls;
    private final GroupMemberManagementService memberService;
    private final GroupManagementSubgroupRepository gmsr;
    private final CrewPositionRepository cpr;
    private final CrewTemplateService templateService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public GroupCompositionDto createStructureFromTemplate(Users user, Long groupId, Long templateId) {

    // find group listing from user request
        GroupListing listing = gls.findGroupListingEntityById(groupId);

    // verify user group authorization
        rankService.verifyUserRankPermissions(user, listing, RankPrivilegeOptions.MANAGE_POSITIONS);

    // find the subgroups that belong to this template
        HashMap<Long, List<CrewSubgroupTemplate>> templateSubgroups = templateService.findSubgroupsByTemplateRootId(templateId)
                .stream()
                .collect(Collectors.groupingBy(sub ->
                        sub.getParentSubgroupId() == null ? ROOT_SUBGROUP_ID : sub.getParentSubgroupId(),
                        HashMap::new,
                        Collectors.toList()));

    // find the positions that belong to this template
        HashMap<Long, List<CrewPositionTemplate>> templatePositions = templateService.findPositionsByTemplateRootId(templateId)
                .stream()
                .collect(Collectors.groupingBy(CrewPositionTemplate::getSubgroupTemplateId,
                        HashMap::new,
                        Collectors.toList()));

    // recursively create real subgroup and position entities, starting from root subgroups...
    // ... that reference this listing.
        List<Long> rootIds = templateSubgroups.get(ROOT_SUBGROUP_ID).stream()
                .map(templateSub -> recurseCreateSubgroupAndPositionsFromTemplate(
                        templateSub, null, null, listing, templateSubgroups, templatePositions))
                .toList();

    // fetch the newly created subgroups
        HashMap<Long, List<GroupManagementSubgroup>> newSubgroups = gmsr.findTreeByRoot(rootIds, listing.getGroupId())
                .stream()
                .collect(Collectors.groupingBy(sub ->
                        sub.getParentSubgroup() == null ? ROOT_SUBGROUP_ID : sub.getParentSubgroup().getSubgroupId(),
                        HashMap::new,
                        Collectors.toList()));

    // fetch the newly created positions
        List<CrewPosition> newPositionsList = cpr.findAllPositionsForSubgroupTrees(rootIds, groupId).stream().toList();

        HashMap<Long, List<CrewPosition>> newPositionsMap = newPositionsList
                .stream()
                .collect(Collectors.groupingBy(p ->
                        p.getSubgroup().getSubgroupId(),
                        HashMap::new,
                        Collectors.toList()));

        return generateGroupCompositionResponseStructure(newSubgroups, newPositionsMap, newPositionsList);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupCompositionDto getExistingGroupComposition(Users user, Long groupId) {
        //method can be used by management and members to display group composition
        // verify only that the requesting user is a member
        memberService.verifyAndReturnUserAsGroupMember(user, groupId);

        //generate a list of the root subgroup ids to build trees from
        List<Long> rootIds = gmsr.findGroupCompositionRootIds(groupId);

        if(rootIds.isEmpty()) {
            return new GroupCompositionDto();
        }

        HashMap<Long, List<GroupManagementSubgroup>> existingSubgroups = gmsr.findTreeByRoot(rootIds, groupId)
                .stream()
                .collect(Collectors.groupingBy(sub ->
                        sub.getParentSubgroup() == null ? ROOT_SUBGROUP_ID : sub.getParentSubgroup().getSubgroupId(),
                        HashMap::new,
                        Collectors.toList()));

        List<CrewPosition> existingPositionsList = cpr.findAllPositionsForSubgroupTrees(rootIds, groupId).stream().toList();

        HashMap<Long, List<CrewPosition>> existingPositionsMap = existingPositionsList
                .stream()
                .collect(Collectors.groupingBy(pos ->
                        pos.getSubgroup().getSubgroupId(),
                        HashMap::new,
                        Collectors.toList()));

        return generateGroupCompositionResponseStructure(existingSubgroups, existingPositionsMap, existingPositionsList);
    }

    private Long recurseCreateSubgroupAndPositionsFromTemplate(CrewSubgroupTemplate template,
                                                               Long rootSubgroupId,
                                                               GroupManagementSubgroup parentSubgroup,
                                                               GroupListing listing,
                                                               HashMap<Long, List<CrewSubgroupTemplate>> subgroups,
                                                               HashMap<Long, List<CrewPositionTemplate>> positions) {

        GroupManagementSubgroup currentNode = gmsr.save(new GroupManagementSubgroup(listing, rootSubgroupId, parentSubgroup, template));
        Long treeRootId = rootSubgroupId == null ? currentNode.getSubgroupId() : rootSubgroupId;

        if(currentNode.getRootSubgroupId() == null) {
            currentNode.setRootSubgroupId(treeRootId);
            gmsr.save(currentNode);
        }

        List<CrewPositionTemplate> nestedPositions = positions.get(template.getTemplateSubgroupId());
        if(nestedPositions != null && !nestedPositions.isEmpty()) {
            nestedPositions.forEach(pos -> cpr.save(new CrewPosition(
                    listing, treeRootId, currentNode, pos)));
        }

        List<CrewSubgroupTemplate> nestedSubgroups = subgroups.get(template.getTemplateSubgroupId());
        if(nestedSubgroups != null && !nestedSubgroups.isEmpty()) {
            nestedSubgroups.forEach(child -> recurseCreateSubgroupAndPositionsFromTemplate(
                    child, treeRootId, currentNode, listing, subgroups, positions));
        }

        return currentNode.getSubgroupId();
    }

    private GroupCompositionDto generateGroupCompositionResponseStructure(HashMap<Long, List<GroupManagementSubgroup>> subgroupMap,
                                                                          HashMap<Long, List<CrewPosition>> positionMap,
                                                                          List<CrewPosition> crewPositions) {
        List<GroupCompositionSubgroupDto> rootSubgroups = subgroupMap.get(ROOT_SUBGROUP_ID).stream()
                .map(subgroup ->
                        getChildrenSubgroupsAndPositions(subgroup, subgroupMap, positionMap))
                .toList();

        List<GroupCompositionCrewPositionDto> crewPositionsDto = crewPositions.stream()
                .map(pos -> modelMapper.map(pos, GroupCompositionCrewPositionDto.class))
                .toList();

        return new GroupCompositionDto(rootSubgroups, crewPositionsDto);
    }

    private GroupCompositionSubgroupDto getChildrenSubgroupsAndPositions(GroupManagementSubgroup currentNode,
                                                                         HashMap<Long, List<GroupManagementSubgroup>> subgroups,
                                                                         HashMap<Long, List<CrewPosition>> crewPositions) {

        List<GroupCompositionCrewPositionDto> levelPositions = crewPositions.getOrDefault(currentNode.getSubgroupId(), List.of())
                .stream()
                .map(pos -> modelMapper.map(pos, GroupCompositionCrewPositionDto.class))
                .toList();

        GroupCompositionSubgroupDto dto = modelMapper.map(currentNode, GroupCompositionSubgroupDto.class);

        dto.setCrewPositions(levelPositions);

        List<GroupCompositionSubgroupDto> treeSubgroups = subgroups.getOrDefault(currentNode.getSubgroupId(), List.of())
                .stream()
                .map(subgroup -> getChildrenSubgroupsAndPositions(subgroup, subgroups, crewPositions)
                ).toList();

        dto.setSubgroups(treeSubgroups);
        return dto;
    }

    @Override
    @Transactional
    public void deleteSubgroup(Users user, Long groupId, Long subgroupId) {
        GroupListing listing = gls.findGroupListingEntityById(groupId);

        GroupManagementSubgroup subgroup = gmsr.findById(subgroupId)
                .orElseThrow(() -> new ResourceNotFoundException("GroupManagementSubgroup", subgroupId));

        gmsr.delete(subgroup);
    }

    @Override
    @Transactional
    public Long assignMemberPosition(Users manager, GroupCompositionCrewPositionDto dto) {
        GroupListing listing =  gls.findGroupListingEntityById(dto.getGroupId());

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_POSITIONS);

        CrewPosition currentPosition = this.cpr.findById(dto.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("CrewPosition", dto.getPositionId()));

        currentPosition.setAssignedMemberUserId(dto.getAssignedMember().getUserSummary().getUserId());

        cpr.save(currentPosition);

        return listing.getGroupId();
    }

    @Override
    @Transactional
    public Long clearMemberPositionAssignment(Users manager, GroupCompositionCrewPositionDto dto) {
        GroupListing listing =  gls.findGroupListingEntityById(dto.getGroupId());

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_POSITIONS);

        CrewPosition currentPosition = this.cpr.findById(dto.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("CrewPosition", dto.getPositionId()));

        currentPosition.setAssignedMemberUserId(null);

        cpr.save(currentPosition);

        return listing.getGroupId();
    }
}
