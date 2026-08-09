package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.AddNewSubgroupRequestDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.CreateOrEditCrewPositionDto;
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

import java.time.Instant;
import java.util.*;
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



        return new GroupCompositionDto(groupId, rootSubgroups);
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
    @Transactional(readOnly = true)
    public List<GroupRoleSummaryDto> getAvailableRoleClassifications(Users manager, Long groupId) {
        GroupListing listing = gls.findGroupListingEntityById(groupId);

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_ROLES);

        return crewRoleService.getRolesForListingByUserId(listing.getGroupId()).stream()
                .map(classification -> modelMapper.map(classification, GroupRoleSummaryDto.class))
                .toList();
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
        List<GroupManagementSubgroup> subgroupEntitiesList = subgroupService.findSubgroupsByGroupId_IncludeDeleted(groupId);
        List<CrewPosition> positionEntitiesList = positionService.findAllPositionsByGroupId_IncludeDeleted(groupId);

        // create subgroup map with same entity instances for faster lookups
        Map<Long, GroupManagementSubgroup> subgroupsMap = subgroupEntitiesList.stream()
                        .collect(Collectors.toMap(GroupManagementSubgroup::getSubgroupId, Function.identity()));

        Set<Long> subgroupEntityIdSet = subgroupsMap.keySet();

        // set of subgroupIds included in the flattened dto
        // will be used to set deleted on all entities that are not in the dto
        Set<Long> subgroupDtoIdSet = flattenedDto.getSubgroups().stream()
                        .map(GroupCompositionSubgroupDto::getSubgroupId)
                            .filter(Objects::nonNull)
                                .collect(Collectors.toSet());

        // find all subgroup entities that are not in the dto
        Set<Long> subgroupsOnlyInEntities = new HashSet<>(subgroupEntityIdSet);
        subgroupsOnlyInEntities.removeAll(subgroupDtoIdSet);

        // soft delete all entities not in the dto
        subgroupsOnlyInEntities.forEach(subId -> {
            GroupManagementSubgroup sub = subgroupsMap.get(subId);
            if(sub != null) {
                sub.setDeletedAt(Instant.now());
                positionService.softDeleteAllBySubgroup(subId);
            }
        });

        flattenedDto.getSubgroups().forEach(subDto -> {

            GroupManagementSubgroup entity = subgroupsMap.get(subDto.getSubgroupId());

            // if the dto contains a subgroup has been soft deleted, restore it
            // this might occur if a user 'undoes' a delete operation
            if(entity == null) {
                GroupManagementSubgroup parentSubgroup = subgroupsMap.get(subDto.getParentSubgroupId());
                entity = new GroupManagementSubgroup(subDto, listing, parentSubgroup);
                subgroupService.saveSubgroup(entity);
            } else if(entity.getDeletedAt() != null) {
                entity.setDeletedAt(null);
            }

            entity.setRootSubgroupId(subDto.getRootSubgroupId());
            entity.setParentSubgroup(subgroupsMap.get(subDto.getParentSubgroupId()));
            entity.setSortOrder(subDto.getSortOrder());
            entity.setSubgroupLabel(subDto.getSubgroupLabel());
        });

        // find roles available within this listing scope for position assignments because the position dto uses a dto
        // (can't assign the role classification from the dto to the position entity's role)
        List<CrewRoleClassification> listingScopedRoles = crewRoleService.getRolesForListingByUserId(listing.getUsers().getUserId());

        // convert to map for faster lookup
        Map<Long, CrewRoleClassification> rolesMap = listingScopedRoles.stream()
                        .collect(Collectors.toMap(CrewRoleClassification::getRoleId, Function.identity()));

        // create positions map with same entity instances from list
        Map<Long, CrewPosition> positionsMap = positionEntitiesList.stream()
                .collect(Collectors.toMap(CrewPosition::getPositionId, Function.identity()));

        // collect all positionIds from the dto into a set for comparison to existing entities
        Set<Long> positionDtoIdSet = flattenedDto.getCrewPositions().stream()
                        .map(GroupCompositionCrewPositionDto::getPositionId)
                                .filter(Objects::nonNull)
                                        .collect(Collectors.toSet());

        // collect all position entities into a set of ids for comparison to the dto
        Set<Long> positionEntityIds = positionsMap.keySet();

        Set<Long> positionsOnlyInEntities = new HashSet<>(positionEntityIds);
        positionsOnlyInEntities.removeAll(positionDtoIdSet);

        positionsOnlyInEntities.forEach(positionId -> {
            CrewPosition inactivePos = positionsMap.get(positionId);

            if(inactivePos != null) {
                inactivePos.setDeletedAt(Instant.now());
                inactivePos.setAssignedMemberUserId(null);
            }
        });

        flattenedDto.getCrewPositions().forEach(positionDto -> {
            CrewPosition entity = positionsMap.get(positionDto.getPositionId());

            if(entity == null) {
                entity = new CrewPosition(listing);
            } else if(entity.getDeletedAt() != null) {
                entity.setDeletedAt(null);
            }

            entity.setSortOrder(positionDto.getSortOrder());
            entity.setSubgroup(subgroupsMap.get(positionDto.getSubgroupId()));
            entity.setRootSubgroupId(positionDto.getRootSubgroupId());
            positionService.restoreOrSkipSoftDeletedPositionMemberAssignment(positionDto, entity);
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
    public GroupCompositionSubgroupDto addSubgroup(Users manager, AddNewSubgroupRequestDto requestDto) {
        GroupListing listing = gls.findGroupListingEntityById(requestDto.getListingId());

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_SUBGROUPS);

        GroupManagementSubgroup parentSubgroup = null;

        if(requestDto.getParentSubgroupId() != null) {
            parentSubgroup = subgroupService.findSubgroupById(requestDto.getParentSubgroupId());
        }

        GroupManagementSubgroup savedSubgroup = subgroupService.saveSubgroup(new GroupManagementSubgroup(requestDto, listing, parentSubgroup));

        Map<Long, CrewRoleClassification> dbRoles = crewRoleService.getRolesForListingByUserId(listing.getUsers().getUserId())
                .stream()
                .collect(Collectors.toMap(
                        CrewRoleClassification::getRoleId,
                        Function.identity()
                ));

        List<CrewPosition> subgroupCrewPositions = new ArrayList<>();

        for(GroupRoleSummaryDto role : requestDto.getPositions()) {
            CrewPosition newPosition = new CrewPosition(listing, savedSubgroup, requestDto.getRootSubgroupId(),
                                                        dbRoles.get(role.getRoleId()));
            subgroupCrewPositions.add(newPosition);
        }

        HashMap<Long, List<CrewPosition>> subgroupPositionsMap = positionService.saveListOf(subgroupCrewPositions)
                .stream()
                .collect(Collectors.groupingBy(pos ->
                        pos.getSubgroup().getSubgroupId(),
                        HashMap::new,
                        Collectors.toList()));

        HashMap<Long, List<GroupManagementSubgroup>> emptySubgroups = new HashMap<>();

        return getChildrenSubgroupsAndPositions(savedSubgroup, emptySubgroups, subgroupPositionsMap);
    }

    @Override
    @Transactional
    public void softDeleteSubgroup(Users manager, Long groupId, Long subgroupId) {
        GroupListing listing = gls.findGroupListingEntityById(groupId);

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_SUBGROUPS);

        subgroupService.softDeleteSubgroup(subgroupId);
    }

    @Override
    public void softDeletePosition(Users manager, Long groupId, Long positionId) {
        GroupListing listing = gls.findGroupListingEntityById(groupId);

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_SUBGROUPS);

        positionService.softDeletePosition(positionId);
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
    public GroupCompositionCrewPositionDto createNewPosition(Users manager, CreateOrEditCrewPositionDto positionDto) {
        GroupListing listing = gls.findGroupListingEntityById(positionDto.getGroupId());

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_POSITIONS);

        GroupManagementSubgroup parentSubgroup = subgroupService.findSubgroupById(positionDto.getSubgroupId());

        CrewRoleClassification crewRole = crewRoleService.findByRoleId(positionDto.getRoleId());

        CrewPosition newPosition = positionService.saveAndFlush(
                new CrewPosition(positionDto, listing, parentSubgroup, crewRole)
        );

        return modelMapper.map(newPosition, GroupCompositionCrewPositionDto.class);
    }

    @Override
    @Transactional
    public GroupCompositionCrewPositionDto editCrewPosition(Users manager, Long positionId,
                                                            CreateOrEditCrewPositionDto positionDto) {

        GroupListing listing = gls.findGroupListingEntityById(positionDto.getGroupId());

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_POSITIONS);

        CrewRoleClassification crewRole = crewRoleService.findByRoleId(positionDto.getRoleId());

        CrewPosition updated = positionService.findById(positionId).map(position -> {
            position.setPositionRole(crewRole);
            position.setPositionNote(positionDto.getPositionNote());
            return positionService.saveAndFlush(position);
        }).orElseThrow(() -> new ResourceNotFoundException("Crew Position", positionId));

        GroupCompositionCrewPositionDto response = modelMapper.map(updated, GroupCompositionCrewPositionDto.class);

        memberService.findGroupMemberCrewPosition(updated.getAssignedMember())
                .map(cp -> modelMapper.map(cp, MemberPositionSummaryDto.class))
                .ifPresent(memberPosition -> response.getAssignedMember().setMemberPosition(memberPosition));

        return response;
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

    // TODO check if prop requires new can be removed
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
