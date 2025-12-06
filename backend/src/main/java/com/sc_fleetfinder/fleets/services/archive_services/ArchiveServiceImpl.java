package com.sc_fleetfinder.fleets.services.archive_services;

import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingArchiveRepository;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.Users;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArchiveServiceImpl implements ArchiveService {

    private final ListingArchiveRepository lar;

    public ArchiveServiceImpl(ListingArchiveRepository lar) {
        this.lar = lar;
    };



    @Override
    @Transactional
    // preparation for moderator deleted records are in mod services
    public void prepareUserDeleteRecords(GroupListing groupListing) {

    };

    // for listings deleted by automod or a user
    @Override
    @Transactional
    public ListingArchive archiveListing(GroupListing listing, ModerationIssue issue,
                               String note) {

        ListingArchive archive = new ListingArchive(listing, issue, note);
        lar.save(archive);

        return archive;
    };

    // for listings deleted manually by a moderator
    @Override
    @Transactional
    public ListingArchive archiveListing(GroupListing listing,
                                                   ModerationIssue issue, String modNote,
                                                   Users mod){

        ListingArchive archive = new ListingArchive(listing, issue, modNote, mod);
        lar.save(archive);

        return archive;
    }
}
