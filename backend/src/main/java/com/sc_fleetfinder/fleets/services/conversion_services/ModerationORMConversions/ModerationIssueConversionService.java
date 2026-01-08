package com.sc_fleetfinder.fleets.services.conversion_services.ModerationORMConversions;

import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModerationIssueResponseDto;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;

public interface ModerationIssueConversionService {

    ModerationIssueResponseDto convertToResponseDto(ModerationIssue entity);
}
