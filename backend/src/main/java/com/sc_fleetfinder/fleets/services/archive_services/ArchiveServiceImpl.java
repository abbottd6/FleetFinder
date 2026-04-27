package com.sc_fleetfinder.fleets.services.archive_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingArchiveRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModerationIssueRepository;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.NotificationOutbox;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
public class ArchiveServiceImpl implements ArchiveService {

    private final ListingArchiveRepository lar;
    private final ModerationIssueRepository mir;
    private final GroupListingRepository groupListingRepository;

    public ArchiveServiceImpl(ListingArchiveRepository lar,
                              ModerationIssueRepository mir, GroupListingRepository groupListingRepository) {
        this.lar = lar;
        this.mir = mir;
        this.groupListingRepository = groupListingRepository;
    };



    // archiving and preparation for deletion of listings by their owner
    // manual mod deletions and AutoMod records preparation is handled in the mod service
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ListingArchive prepareUserDeleteRecords(GroupListing listing, Users user) {
        ModerationIssue issue;
        String note;

        //check for existing moderation issue
        Optional<ModerationIssue> existing = mir.findByGroupRef(listing);

        if(existing.isPresent()) {
            issue = existing.get();
            note = "User deleted listing while issue status still 'Pending'.";
        } else {
            issue = new ModerationIssue(listing, user);
            issue.setStatus("No Reports");
            mir.save(issue);
            note = "User deleted listing with no recorded moderation issue.";
        }

        String actionType = "None";

        log.info("Archive records generated for listing: {}, in response to user delete.",
                listing.getGroupId());

        return new ListingArchive(listing, issue, actionType, note);
    }

//    @Override
//    @Transactional
//    public ListingArchive prepareAutomatedDeleteRecords(NotificationOutbox obEntity) {
//        ModerationIssue issue;
//        ListingArchive archive;
//        String note;
//
//        GroupListing listing = groupListingRepository.findById(obEntity.getEntityId())
//                .orElseThrow(() -> new ResourceNotFoundException("GroupListing", obEntity.getEntityId()));
//
//        //check for existing moderation issue
//        Optional<ModerationIssue> existing = mir.findByGroupRef(listing);
//
//        if(existing.isPresent()) {
//            issue = existing.get();
//            note = "User deleted listing while issue status still 'Pending'.";
//        } else {
//            issue = new ModerationIssue(listing, obEntity.getEntityOwner());
//            issue.setStatus("No Reports");
//            mir.save(issue);
//            mir.flush();
//            note = "User deleted listing with no recorded moderation issue.";
//        }
//
//        archive = archiveListing(listing, issue, note);
//        lar.save(archive);
//
//        log.info("Archive records generated for listing: {}, in response to user delete.",
//                listing.getGroupId());
//
//        return archive;
//    }

    // for listings deleted by automod or a user
    @Override
    @Transactional
    public ListingArchive archiveListing(GroupListing listing, ModerationIssue issue,
                               String actionType, String note) {

        ListingArchive archive = new ListingArchive(listing, issue, actionType, note);
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

    @Override
    @Transactional
    public ListingArchive archiveListing(ListingArchive transientArchive) {
        return lar.save(transientArchive);
    }
}
