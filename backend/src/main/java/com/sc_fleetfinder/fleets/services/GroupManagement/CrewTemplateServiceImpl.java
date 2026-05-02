package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DAO.GroupManagement.CrewTemplateRepository;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.CrewTemplateSummaryDto;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewTemplate;
import com.sc_fleetfinder.fleets.entities.Users;
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
    private final ModelMapper modelMapper;

    @Override
    public List<CrewTemplateSummaryDto> fetchTemplateSummariesForUser(Users user) {
        List<CrewTemplate> myTemplates = crewTemplateRepo.fetchTemplateSummariesForUser(user.getUserId());

        return myTemplates.stream()
                .map(template -> modelMapper.map(template, CrewTemplateSummaryDto.class))
                .toList();
    }
}
