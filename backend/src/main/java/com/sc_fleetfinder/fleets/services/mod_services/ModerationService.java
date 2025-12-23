package com.sc_fleetfinder.fleets.services.mod_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.ManualModDeleteDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.ModClearIssueDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModerationIssueResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ModerationService {

    List<GroupListingResponseDto> modGetAllGroupListings();
    Page<ModerationIssueResponseDto> modGetAllIssues(Pageable pageable);
    ResponseEntity<?> modClearIssue(ModClearIssueDto dto, Users requestingMod);
    ResponseEntity<?> modDeleteListing(@Valid ManualModDeleteDto dto, Users mod);
    void prepareManualModRemovalRecords(ManualModDeleteDto dto, Users mod);
    void prepareAutoModRemovalRecords(ModerationIssue issue);
    void autoModDeleteListing(GroupListing listing, Users owner);
    ModerationIssue generateModerationIssue(GroupListing groupListing, String status);
    void updateOrCreateUserModerationRecord(GroupListing listing);
}
