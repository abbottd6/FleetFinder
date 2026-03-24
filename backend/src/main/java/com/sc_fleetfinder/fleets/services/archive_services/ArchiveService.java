package com.sc_fleetfinder.fleets.services.archive_services;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.entities.Users;

public interface ArchiveService {

    void prepareUserDeleteRecords(GroupListing groupListing, Users user);
    ListingArchive archiveListing(GroupListing listing, ModerationIssue issue,
                                  String actionType, String note);
    ListingArchive archiveListing(GroupListing listing, ModerationIssue issue,
                                  String modNote, Users mod);
}
