package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewPositionTemplateRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewSubgroupTemplateRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewTemplateRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.TemplateFromCompRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.CrewTemplateSummaryDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionCrewPositionDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionSubgroupDto;
import com.sc_fleetfinder.fleets.constants.TemplateConstants;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPositionTemplate;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewRoleClassification;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewSubgroupTemplate;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewTemplate;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ContentLimitException;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupListingService;
import com.sc_fleetfinder.fleets.utils.GroupManagement.CrewTemplateCategory;
import com.sc_fleetfinder.fleets.utils.GroupManagement.RankPrivilegeOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrewTemplateServiceImpl implements CrewTemplateService {

    private final CrewTemplateRepository crewTemplateRepo;
    private final CrewSubgroupTemplateRepository subgroupTemplateRepo;
    private final CrewPositionTemplateRepository positionTemplateRepo;
    private final InGroupRankService rankService;
    private final GroupListingService gls;
    private final CrewRoleService crewRoleService;
    private final ModelMapper modelMapper;

    @Override
    public List<CrewTemplateSummaryDto> fetchTemplateSummariesForUser(Users user) {
        List<CrewTemplate> myTemplates = crewTemplateRepo.fetchTemplateSummariesForUser(user.getUserId());

        return myTemplates.stream()
                .map(template -> modelMapper.map(template, CrewTemplateSummaryDto.class))
                .toList();
    }

    @Override
    @Transactional
    public String createTemplateFromCompositionDto(Users manager, TemplateFromCompRequestDto templateFromDto) {
        GroupListing listing = gls.findGroupListingEntityById(templateFromDto.getGroupId());

        rankService.verifyUserRankPermissions(manager, listing, RankPrivilegeOptions.MANAGE_SUBGROUPS);

        Integer existingCount = crewTemplateRepo.countUserCustomTemplates(manager.getUserId());

        if(existingCount >= TemplateConstants.USER_CUSTOM_TEMPLATE_LIMIT) {
            throw new ContentLimitException(manager.getUserId(), "Custom Group Composition Template",
                    TemplateConstants.USER_CUSTOM_TEMPLATE_LIMIT);
        }

        CrewTemplateCategory CUSTOM = CrewTemplateCategory.Custom;
        Map<Long, CrewRoleClassification> groupAvailableRoles = crewRoleService.constructCrewRolesMapForGroup(listing.getUsers().getUserId());

        CrewTemplate templateRoot = crewTemplateRepo.save(new CrewTemplate(templateFromDto.getTemplateLabel(), CUSTOM, manager));

        List<CrewPositionTemplate> crewPositionTemplates = new ArrayList<>();

        for(GroupCompositionSubgroupDto subgroupDto : templateFromDto.getSubgroups()) {
            recursivelyMapGroupCompDtoToTemplates(templateRoot.getTemplateId(), null, subgroupDto,
                                                  crewPositionTemplates, groupAvailableRoles);
        }

        positionTemplateRepo.saveAll(crewPositionTemplates);

        return templateRoot.getTemplateLabel();
    }

    @Override
    public CrewTemplate findTemplateTreeRootById(Long templateId) {
        return crewTemplateRepo.findTemplateAndChildrenById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("CrewTemplate", templateId));
    }

    @Override
    public List<CrewSubgroupTemplate> findSubgroupsByTemplateRootId(Long templateId) {
        return subgroupTemplateRepo.findByTemplateId(templateId);
    }

    @Override
    public List<CrewPositionTemplate> findPositionsByTemplateRootId(Long templateId) {
        return positionTemplateRepo.findByTemplateRootId(templateId);
    }

    private void recursivelyMapGroupCompDtoToTemplates(Long templateRootId,
                                                                             Long parentSubgroupId,
                                                                             GroupCompositionSubgroupDto subgroupDto,
                                                                             List<CrewPositionTemplate> positionTemplates,
                                                                             Map<Long, CrewRoleClassification> availableRoles) {

        CrewSubgroupTemplate currentSubgroupTemplate = this.subgroupTemplateRepo.save(new CrewSubgroupTemplate(templateRootId,
                parentSubgroupId, subgroupDto));

        positionTemplates.addAll(subgroupDto.getCrewPositions().stream()
                .map(p -> {
                    CrewRoleClassification role = availableRoles.get(p.getGroupRole().getRoleId());
                    return new CrewPositionTemplate(templateRootId, currentSubgroupTemplate.getTemplateSubgroupId(),
                                                    role, p.getPositionNote(), p.getSortOrder());
                })
                .toList()
        );

        for(GroupCompositionSubgroupDto subgroup : subgroupDto.getSubgroups()) {
            recursivelyMapGroupCompDtoToTemplates(templateRootId, currentSubgroupTemplate.getTemplateSubgroupId(),
                                                  subgroup, positionTemplates, availableRoles);
        }
    }
}
