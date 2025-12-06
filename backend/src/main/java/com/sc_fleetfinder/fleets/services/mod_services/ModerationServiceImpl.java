package com.sc_fleetfinder.fleets.services.mod_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingArchiveRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingReportRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModListingActionRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModerationIssueRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.UserModerationRecordRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.ManualModDeleteDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReport;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.UserModerationRecord;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.events.ListingAutoDeleteEvent;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupListingServiceImpl;
import com.sc_fleetfinder.fleets.services.archive_services.ArchiveService;
import com.sc_fleetfinder.fleets.services.conversion_services.GroupListingConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ModerationServiceImpl implements ModerationService {

    @Autowired
    private ArchiveService archiveService;

    private static final Logger log = LoggerFactory.getLogger(GroupListingServiceImpl.class);
    private final GroupListingRepository glr;
    private final GroupListingConversionService glcs;
    private final UserRepository userRepo;
    private final UserModerationRecordRepository umrr;
    private final ListingArchiveRepository lar;
    private final ListingReportRepository lrr;
    private final ModListingActionRepository mlar;
    private final ApplicationEventPublisher eventPublisher;
    private final ModerationIssueRepository mir;

    public ModerationServiceImpl(GroupListingRepository groupListingRepository,
                                 GroupListingConversionService groupListingConversionService,
                                 UserRepository userRepo,
                                 UserModerationRecordRepository umrr,
                                 ListingArchiveRepository lar,
                                 ListingReportRepository lrr,
                                 ModListingActionRepository mlar,
                                 ModerationIssueRepository mir,
                                 ApplicationEventPublisher eventPublisher) {
        this.glr = groupListingRepository;
        this.glcs = groupListingConversionService;
        this.userRepo = userRepo;
        this.umrr = umrr;
        this.lar = lar;
        this.lrr = lrr;
        this.mlar = mlar;
        this.mir = mir;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<GroupListingResponseDto> modGetAllGroupListings() {
        List<GroupListing> groupListings = glr.findAll();

        if(groupListings.isEmpty()) {
            log.info("No group listings found.");
        }

        return groupListings.stream()
                .map(glcs::convertListingToResponseDto)
                .collect(Collectors.toList());
    }

    // ##TODO: change this to use the mod specific delete dto so that it includes a deletion/report basis and a note field
    @Override
    @Transactional(transactionManager = "transactionManager")
    public ResponseEntity<?> modDeleteListing(ManualModDeleteDto dto, Users mod) {
        try {
            GroupListing groupEntity = glr.findById(dto.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException(dto.getGroupId()));

            prepareManualModRemovalRecords(dto, mod);

            glr.flush();
            glr.delete(groupEntity);

            Map<String, String> response = new HashMap<>();
            response.put("listingTitle", groupEntity.getListingTitle());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (Exception e) {
            log.error("modDeleteGroupListing failed. Reason: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void prepareManualModRemovalRecords(ManualModDeleteDto dto, Users mod) {
        //verify and get listing
        GroupListing condemned = glr.findById(dto.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("GroupListing", dto.getGroupId()));

        //get the listing owner
        Users owner = userRepo.findById(condemned.getUsers().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Users", condemned.getUsers().getUserId()));

        //check for existing ModerationIssue or make a new one
    };

    @Override
    @Transactional
    public void prepareAutoModRemovalRecords(ModerationIssue issue) {
        // verify and get user
        Users owner = userRepo.findById(issue.getUserRef().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Users",
                        issue.getUserRef().getUserId()));

        // verify and get listing
        GroupListing condemned = glr.findById(issue.getGroupRef().getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "GroupListing", issue.getGroupRef().getGroupId()));

        Set<ListingReport> reports = lrr.findByModIssueRef(issue);
        reports.forEach(report -> report.setStatus("Actioned"));
        lrr.saveAll(reports);

        String note = "Listing was deleted by AutoMod with a total report count of: " +
                issue.getReportTotalCount() +", " +
                "Corresponding issueId: " + issue.getIssueId();

        //archive listing issue/reports data and user input fields from listing
        ListingArchive archive = archiveService.archiveListing(condemned, issue, note);

        recordModeratorAction(issue, note, archive);

        updateOrCreateUserModerationRecord(condemned);

        eventPublisher.publishEvent(new ListingAutoDeleteEvent(condemned, owner));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void autoModDeleteListing(GroupListing condemned, Users owner) {

        owner.getGroupListings().remove(condemned);
        userRepo.save(owner);

        glr.delete(condemned);
        log.info("Auto-mod deleted listing: {}", condemned.getListingTitle());
    }

    @Override
    @Transactional
    public ModerationIssue generateModerationIssue(GroupListing listing) {
        //verify that the ModerationIssue does not already exist
        if(mir.findByGroupRef(listing).isPresent()) {
            throw new IllegalStateException(
                    "ModerationIssue already exists for listing: " + listing.getGroupId()
            );
        }

        ModerationIssue issue = new ModerationIssue(listing, listing.getUsers());
        mir.save(issue);
        mir.flush();
        log.info("New ModerationIssue created with id: {} for group: {}",
                issue.getIssueId(), listing.getGroupId());

        return issue;
    }

    @Override
    @Transactional
    public void updateOrCreateUserModerationRecord(GroupListing listing) {
        Users offendingUser = userRepo.findById(listing.getUsers().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Users", listing.getUsers().getUserId()));

        UserModerationRecord record = umrr.findByUser(offendingUser)
                .orElseGet(() -> new UserModerationRecord(offendingUser));

        int count = record.getModActionCount();
        record.setModActionCount(count + 1);

        umrr.save(record);

        if(record.getLastModActionTs() == null) {
            log.info(
                    "User report resulted in an AutoMod deletion of group: {}.\n" +
                    "A new moderation record has been generated for the user because " +
                    "an existing record was not found.", listing.getGroupId());
        } else {
            log.info("User report resulted in AutoMod deletion of group: {}." +
                    "The user's existing moderation record has been updated.", listing.getGroupId());
        }
    }

    @Transactional
    protected void recordModeratorAction(ModerationIssue issue, String note,
                                         ListingArchive archive) {
        ModListingAction modAction = new ModListingAction(issue, note, archive);

        mlar.save(modAction);
        log.info("Moderator action recorded under actionId: {}", modAction.getActionId());
    }
}
