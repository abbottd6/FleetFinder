package com.sc_fleetfinder.fleets.services.archive_services;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;

public interface ArchiveService {

    void prepareUserDeleteRecords(GroupListing groupListing);
    ListingArchive archiveListing(GroupListing listing, ModerationIssue issue, String note);
}
