package com.sc_fleetfinder.fleets.services.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.CrewTemplateSummaryDto;
import com.sc_fleetfinder.fleets.entities.Users;

import java.util.List;

public interface CrewTemplateService {

    List<CrewTemplateSummaryDto> fetchTemplateSummariesForUser(Users user);
}
