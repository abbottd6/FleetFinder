package com.sc_fleetfinder.fleets.services.reporting_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.SubmitListingReportDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

public interface ListingReportingService {

    ResponseEntity<?> generateListingReport(@Validated SubmitListingReportDto dto, Users user);
    ModerationIssue generateModerationIssue(GroupListing groupListing);
    ResponseEntity<?> getUsersReportBrief(Users user);
}
