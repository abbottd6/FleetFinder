package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewPositionTemplateRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewSubgroupTemplateRepository;
import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewTemplateRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.CrewTemplateSummaryDto;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPositionTemplate;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewSubgroupTemplate;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewTemplate;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrewTemplateServiceImpl implements CrewTemplateService {

    private final CrewTemplateRepository crewTemplateRepo;
    private final CrewSubgroupTemplateRepository subgroupTemplateRepo;
    private final CrewPositionTemplateRepository positionTemplateRepo;
    private final ModelMapper modelMapper;

    @Override
    public List<CrewTemplateSummaryDto> fetchTemplateSummariesForUser(Users user) {
        List<CrewTemplate> myTemplates = crewTemplateRepo.fetchTemplateSummariesForUser(user.getUserId());

        return myTemplates.stream()
                .map(template -> modelMapper.map(template, CrewTemplateSummaryDto.class))
                .toList();
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
}
