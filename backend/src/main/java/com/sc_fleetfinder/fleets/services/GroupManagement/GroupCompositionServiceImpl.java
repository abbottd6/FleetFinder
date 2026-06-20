package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.UpdateSubgroupDropListOrientationDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.*;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sc_fleetfinder.fleets.entities.GroupManagement.GroupManagementConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupCompositionServiceImpl implements GroupCompositionService {

    private final InGroupRankService rankService;
    private final GroupListingService gls;
    private final GroupMemberManagementService memberService;
    private final SubgroupManagementService subgroupService;
    private final PositionManagementService positionService;
    private final CrewRoleService crewRoleService;
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
        HashMap<Long, List<GroupManagementSubgroup>> newSubgroups = subgroupService.findTreeMapByRootId(rootIds, listing.getGroupId());

    // fetch the newly created positions
        List<CrewPosition> newPositionsList = positionService.findAllPositionsForSubgroupTrees(rootIds, groupId);

        HashMap<Long, List<CrewPosition>> newPositionsMap = newPositionsList
                .stream()
                .collect(Collectors.groupingBy(p ->
                        p.getSubgroup().getSubgroupId(),
                        HashMap::new,
                        Collectors.toList()));

        return generateGroupCompositionResponseStructure(newSubgroups, newPositionsMap, newPositionsList, groupId);
    }

    @Override
    @Transactional(readOnly = true)
    public GroupCompositionDto getExistingGroupComposition(Users user, Long groupId) {
        //method can be used by management and members to display group composition
        // verify only that the requesting user is a member
        memberService.verifyAndReturnUserAsGroupMember(user, groupId);

        //generate a list of the root subgroup ids to build trees from
        List<Long> rootIds = subgroupService.findGroupCompositionRootIds(groupId);

        if(rootIds.isEmpty()) {
            return new GroupCompositionDto(groupId);
        }

        //build map with parent subgroup id as key and list of child subgroups within each parent
        HashMap<Long, List<GroupManagementSubgroup>> existingSubgroups = subgroupService.findTreeMapByRootId(rootIds, groupId);

        List<CrewPosition> existingPositionsList = positionService.findAllPositionsForSubgroupTrees(rootIds, groupId).stream().toList();

        HashMap<Long, List<CrewPosition>> existingPositionsMap = existingPositionsList
                .stream()
                .collect(Collectors.groupingBy(pos ->
                        pos.getSubgroup().getSubgroupId(),
                        HashMap::new,
                        Collectors.toList()));

        return generateGroupCompositionResponseStructure(existingSubgroups, existingPositionsMap, existingPositionsList, groupId);
    }

    private Long recurseCreateSubgroupAndPositionsFromTemplate(CrewSubgroupTemplate template,
                                                               Long rootSubgroupId,
                                                               GroupManagementSubgroup parentSubgroup,
                                                               GroupListing listing,
                                                               HashMap<Long, List<CrewSubgroupTemplate>> subgroups,
                                                               HashMap<Long, List<CrewPositionTemplate>> positions) {

        GroupManagementSubgroup currentNode = subgroupService.saveSubgroup(new GroupManagementSubgroup(listing,
                rootSubgroupId, parentSubgroup, template));

        Long treeRootId = rootSubgroupId == null ? currentNode.getSubgroupId() : rootSubgroupId;

        if(currentNode.getRootSubgroupId() == null) {
            currentNode.setRootSubgroupId(treeRootId);
            subgroupService.saveSubgroup(currentNode);
        }

        List<CrewPositionTemplate> nestedPositions = positions.get(template.getTemplateSubgroupId());
        if(nestedPositions != null && !nestedPositions.isEmpty()) {
            nestedPositions.forEach(pos -> positionService.saveAndFlush(new CrewPosition(
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
                                                                          List<CrewPosition> crewPositions, Long groupId) {
        List<GroupCompositionSubgroupDto> rootSubgroups = subgroupMap.get(ROOT_SUBGROUP_ID).stream()
                .map(subgroup ->
                        getChildrenSubgroupsAndPositions(subgroup, subgroupMap, positionMap))
                .toList();

        List<GroupCompositionCrewPositionDto> crewPositionsDto = crewPositions.stream()
                .map(pos -> modelMapper.map(pos, GroupCompositionCrewPositionDto.class))
                .toList();



        return new GroupCompositionDto(groupId, rootSubgroups, crewPositionsDto);
    }

    private GroupCompositionSubgroupDto getChildrenSubgroupsAndPositions(GroupManagementSubgroup currentNode,
                                                                         HashMap<Long, List<GroupManagementSubgroup>> subgroups,
                                                                         HashMap<Long, List<CrewPosition>> crewPositions) {

        List<GroupCompositionCrewPositionDto> levelPositions = crewPositions.getOrDefault(currentNode.getSubgroupId(), List.of())
                .stream()
                .map(pos -> {
                    GroupCompositionCrewPositionDto dto = modelMapper.map(pos, GroupCompositionCrewPositionDto.class);
                    memberService.findGroupMemberCrewPosition(pos.getAssignedMember())
                            .map(cp -> modelMapper.map(cp, MemberPositionSummaryDto.class))
                            .ifPresent(memberPosition -> dto.getAssignedMember().setMemberPosition(memberPosition));
                    return dto;
                }).toList();

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
    public void updateGroupCompositionState(Users manager, GroupCompositionDto groupCompDto) {
        Long groupId = groupCompDto.getGroupId();

        GroupListing listing = gls.findGroupListingEntityById(groupId);

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_SUBGROUPS);

        // flatten the dto for mapping
        FlattenedGroupCompDto flattenedDto = flattenGroupCompositionDtoSubgroupsAndPositions(groupCompDto);

        // fetch the database subgroups and roles
        List<GroupManagementSubgroup> subgroupEntitiesList = subgroupService.findSubgroupsByGroupId(groupId);
        List<CrewPosition> positionEntitiesList = positionService.findAllPositionsByGroupId(groupId);

        // create subgroup map with same entity instances for faster lookups
        Map<Long, GroupManagementSubgroup> subgroupsMap = subgroupEntitiesList.stream()
                        .collect(Collectors.toMap(GroupManagementSubgroup::getSubgroupId, Function.identity()));

        flattenedDto.getSubgroups().forEach(subDto -> {

            GroupManagementSubgroup entity = subgroupsMap.get(subDto.getSubgroupId());

            // if the dto contains a subgroup that is not in db, create it
            // this might occur if a user 'undoes' a delete operation
            if(entity == null) {
                GroupManagementSubgroup parentSubgroup = subgroupsMap.get(subDto.getParentSubgroupId());
                entity = new GroupManagementSubgroup(subDto, listing, parentSubgroup);
                subgroupService.saveSubgroup(entity);
            };

            entity.setRootSubgroupId(subDto.getRootSubgroupId());
            entity.setParentSubgroup(subgroupsMap.get(subDto.getParentSubgroupId()));
            entity.setSortOrder(subDto.getSortOrder());
            entity.setSubgroupLabel(subDto.getSubgroupLabel());
        });

        // if the db contains a subgroup that is not in the dto tree, delete it.
        // this might occur if a user undoes a create operation
//        subgroupsMap.values().forEach(subgroup -> {
//            if()
//        })

        // find roles available within this listing scope for position assignments because the position dto uses a dto
        // (can't assign the role classification from the dto to the position entity's role)
        List<CrewRoleClassification> listingScopedRoles = crewRoleService.getRolesForListingByUserId(listing.getUsers().getUserId());

        // convert to map for faster lookup
        Map<Long, CrewRoleClassification> rolesMap = listingScopedRoles.stream()
                        .collect(Collectors.toMap(CrewRoleClassification::getRoleId, Function.identity()));

        // create positions map with same entity instances from list
        Map<Long, CrewPosition> positionsMap = positionEntitiesList.stream()
                .collect(Collectors.toMap(CrewPosition::getPositionId, Function.identity()));

        flattenedDto.getCrewPositions().forEach(positionDto -> {
            CrewPosition entity = positionsMap.get(positionDto.getPositionId());
            if(entity == null) return;

            entity.setSortOrder(positionDto.getSortOrder());
            entity.setSubgroup(subgroupsMap.get(positionDto.getSubgroupId()));
            entity.setRootSubgroupId(positionDto.getRootSubgroupId());
            entity.setAssignedMemberUserId(positionDto.getAssignedMember() != null
                            ? positionDto.getAssignedMember().getUserSummary().getUserId()
                            : null);
            entity.setPositionRole(positionDto.getGroupRole() != null
                    ? rolesMap.get(positionDto.getGroupRole().getRoleId())
                    : null);
        });

        // saves are managed by context to ensure roleback on any failure
    }

    @Override
    public FlattenedGroupCompDto flattenGroupCompositionDtoSubgroupsAndPositions(GroupCompositionDto groupCompDto) {
        FlattenedGroupCompDto flattenedGroupCompDto = new FlattenedGroupCompDto();

        if(groupCompDto.getSubgroups() != null && !groupCompDto.getSubgroups().isEmpty()) {
            groupCompDto.getSubgroups().forEach(subgroup -> {
                subgroup.setRootSubgroupId(subgroup.getSubgroupId());
                subgroup.setParentSubgroupId(null);

                Long rootSubgroupId = subgroup.getRootSubgroupId();
                Long selfSubgroupId = subgroup.getSubgroupId();

                subgroup.getCrewPositions().forEach(crewPosition -> {
                    crewPosition.setSubgroupId(selfSubgroupId);
                    crewPosition.setRootSubgroupId(rootSubgroupId);
                });

                flattenedGroupCompDto.getSubgroups().add(subgroup);

                FlattenedGroupCompDto flattenedSubgroup = recursivelyFlattenCompDto(subgroup, rootSubgroupId, selfSubgroupId);

                flattenedGroupCompDto.getCrewPositions().addAll(flattenedSubgroup.getCrewPositions());
                flattenedGroupCompDto.getSubgroups().addAll(flattenedSubgroup.getSubgroups());
            });
        }

        return flattenedGroupCompDto;
    }

    private FlattenedGroupCompDto recursivelyFlattenCompDto(GroupCompositionSubgroupDto subgroup,
                                                            Long rootSubgroupId,
                                                            Long parentSubgroupId) {
        FlattenedGroupCompDto flattenedGroupCompDto = new FlattenedGroupCompDto();

        if(subgroup.getCrewPositions() != null && !subgroup.getCrewPositions().isEmpty()) {
            flattenedGroupCompDto.getCrewPositions().addAll(subgroup.getCrewPositions());
        }

        if(subgroup.getSubgroups() != null && !subgroup.getSubgroups().isEmpty()) {
            subgroup.getSubgroups().forEach(childSubgroup -> {
                childSubgroup.setRootSubgroupId(rootSubgroupId);
                childSubgroup.setParentSubgroupId(parentSubgroupId);

                childSubgroup.getCrewPositions().forEach(crewPosition -> {
                    crewPosition.setSubgroupId(childSubgroup.getSubgroupId());
                    crewPosition.setRootSubgroupId(rootSubgroupId);
                });

                flattenedGroupCompDto.getSubgroups().add(childSubgroup);

                Long selfSubgroupId = childSubgroup.getSubgroupId();
                FlattenedGroupCompDto flattenedSubgroup = recursivelyFlattenCompDto(childSubgroup, rootSubgroupId, selfSubgroupId);

                flattenedGroupCompDto.getCrewPositions().addAll(flattenedSubgroup.getCrewPositions());
                flattenedGroupCompDto.getSubgroups().addAll(flattenedSubgroup.getSubgroups());
            });
        }

        return flattenedGroupCompDto;
    }

    @Override
    @Transactional
    public void deleteSubgroup(Users user, Long groupId, Long subgroupId) {

        GroupManagementSubgroup subgroup = subgroupService.findSubgroupById(subgroupId);

        subgroupService.deleteSubgroup(subgroup);
    }

    @Override
    @Transactional
    public void updateSubgroupDropListOrientation(Users manager, UpdateSubgroupDropListOrientationDto dto) {
        GroupListing listing =  gls.findGroupListingEntityById(dto.getGroupId());

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_SUBGROUPS);

        GroupManagementSubgroup update = subgroupService.findSubgroupById(dto.getSubgroupId());

        update.setDropListOrientation(dto.getOrientation());

        subgroupService.saveSubgroup(update);
    }

    @Override
    @Transactional
    public Long assignMemberPosition(Users manager, GroupCompositionCrewPositionDto dto) {
        GroupListing listing =  gls.findGroupListingEntityById(dto.getGroupId());

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_POSITIONS);

        CrewPosition currentPosition = this.positionService.findById(dto.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("CrewPosition", dto.getPositionId()));

        Users assigneeAsUser = userService.findUserById(dto.getAssignedMember().getUserSummary().getUserId());

        GroupMember assigneeAsMember = memberService.verifyAndReturnUserAsGroupMember(assigneeAsUser, listing.getGroupId());

        Optional<CrewPosition> wasPreviouslyAssigned = memberService.findGroupMemberCrewPosition(assigneeAsMember);

        wasPreviouslyAssigned.ifPresent(this::clearPositionAssignedMemberTransaction);

        currentPosition.setAssignedMemberUserId(dto.getAssignedMember().getUserSummary().getUserId());

        positionService.saveAndFlush(currentPosition);

        return listing.getGroupId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void clearPositionAssignedMemberTransaction(CrewPosition position) {
        position.setAssignedMemberUserId(null);
        positionService.saveAndFlush(position);
    }

    @Override
    @Transactional
    public Long clearMemberPositionAssignment(Users manager, GroupCompositionCrewPositionDto dto) {
        GroupListing listing =  gls.findGroupListingEntityById(dto.getGroupId());

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_POSITIONS);

        CrewPosition currentPosition = this.positionService.findById(dto.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("CrewPosition", dto.getPositionId()));

        currentPosition.setAssignedMemberUserId(null);

        positionService.saveAndFlush(currentPosition);

        return listing.getGroupId();
    }

    @Override
    @Transactional
    public GroupManagerMemberResponseDto clearPositionAssignmentByMember(Users manager, GroupManagerMemberResponseDto dto) {
        GroupListing listing = gls.findGroupListingEntityById(dto.getListingId());

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_POSITIONS);

        CrewPosition assignedTo = this.positionService.findById(dto.getMemberPosition().getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("CrewPosition", dto.getMemberPosition().getPositionId()));

        assignedTo.setAssignedMemberUserId(null);
        positionService.saveAndFlush(assignedTo);

        dto.setMemberPosition(null);

        return dto;
    }

    @Override
    public void updateSubgroupLabelNoReturn(Users manager, Long subgroupId, String newLabel) {
        GroupManagementSubgroup subgroup = subgroupService.findSubgroupById(subgroupId);

        rankService.verifyUserRankPermissions(manager, subgroup.getGroupListing(), RankPrivilegeOptions.MANAGE_SUBGROUPS);

        subgroupService.updateSubgroupLabel(subgroup, newLabel);
    }
}
