package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.CrewTemplateSummaryDto;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewPositionTemplate;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewSubgroupTemplate;
import com.sc_fleetfinder.fleets.entities.GroupManagement.CrewTemplate;
import com.sc_fleetfinder.fleets.entities.Users;

import java.util.List;

public interface CrewTemplateService {

    List<CrewTemplateSummaryDto> fetchTemplateSummariesForUser(Users user);

    CrewTemplate findTemplateTreeRootById(Long templateId);

    List<CrewSubgroupTemplate> findSubgroupsByTemplateRootId(Long templateId);

    List<CrewPositionTemplate> findPositionsByTemplateRootId(Long templateId);
}
