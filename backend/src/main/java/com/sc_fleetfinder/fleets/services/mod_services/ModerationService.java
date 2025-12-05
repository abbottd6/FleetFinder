package com.sc_fleetfinder.fleets.services.mod_services;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.DeleteGroupListingDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ModerationService {

    List<GroupListingResponseDto> modGetAllGroupListings();
    ResponseEntity<?> modDeleteListing(@Valid DeleteGroupListingDto modDeleteDto);
    void prepareAutoModRemovalRecords(ModerationIssue issue);
    void autoModDeleteListing(GroupListing listing, Users owner);
    void updateOrCreateUserModerationRecord(GroupListing listing);
    ListingArchive archiveActionedListing(GroupListing listing, ModerationIssue issue, String note);
}
