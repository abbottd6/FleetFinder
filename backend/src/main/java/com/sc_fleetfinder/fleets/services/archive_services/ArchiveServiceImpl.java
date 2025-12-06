package com.sc_fleetfinder.fleets.services.archive_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingArchiveRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingReportRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class ArchiveServiceImpl implements ArchiveService {

    private final ListingArchiveRepository lar;
    private final UserRepository userRepo;
    private final GroupListingRepository glr;
    private final ListingReportRepository lrr;

    public ArchiveServiceImpl(ListingArchiveRepository lar, UserRepository userRepo,
                              GroupListingRepository glr, ListingReportRepository lrr) {
        this.lar = lar;
        this.userRepo = userRepo;
        this.glr = glr;
        this.lrr = lrr;
    };



    @Override
    @Transactional
    // preparation for moderator deleted records are in mod services
    public void prepareUserDeleteRecords(GroupListing groupListing) {

    };

    @Override
    @Transactional
    public ListingArchive archiveListing(GroupListing listing, ModerationIssue issue,
                               String note) {

        ListingArchive archive = new ListingArchive(listing, issue, note);
        lar.save(archive);

        return archive;
    };
}
