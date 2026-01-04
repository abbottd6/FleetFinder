package com.sc_fleetfinder.fleets.services.mod_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingReportBasisRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingReportRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModListingActionRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModerationIssueRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.UserModerationRecordRepository;
import com.sc_fleetfinder.fleets.DAO.UserRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.ManualModDeleteDto;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.ModClearIssueDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupListingResponseDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModListingActionDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.ModerationIssueResponseDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingArchive;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReport;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReportBasis;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModListingAction;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.UserModerationRecord;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.events.ListingModDeleteEvent;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.CRUD_services.GroupListingServiceImpl;
import com.sc_fleetfinder.fleets.services.archive_services.ArchiveService;
import com.sc_fleetfinder.fleets.services.conversion_services.GroupListingConversionService;
import com.sc_fleetfinder.fleets.services.conversion_services.ModerationORMConversions.ModListingActionConversionService;
import com.sc_fleetfinder.fleets.services.conversion_services.ModerationORMConversions.ModerationIssueConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationConstants.AUTO_MOD_REPORT_THRESHOLD;

@Service
public class ModerationServiceImpl implements ModerationService {

    @Autowired
    private ArchiveService archiveService;

    private static final Logger log = LoggerFactory.getLogger(GroupListingServiceImpl.class);
    private final GroupListingRepository glr;
    private final GroupListingConversionService glcs;
    private final UserRepository userRepo;
    private final UserModerationRecordRepository umrr;
    private final ListingReportRepository lrr;
    private final ModListingActionRepository mlar;
    private final ApplicationEventPublisher eventPublisher;
    private final ModerationIssueRepository mir;
    private final ModerationIssueConversionService mics;
    private final ModListingActionConversionService mlacs;
    private final ListingReportBasisRepository lrbr;

    public ModerationServiceImpl(GroupListingRepository groupListingRepository,
                                 GroupListingConversionService groupListingConversionService,
                                 UserRepository userRepo,
                                 UserModerationRecordRepository umrr,
                                 ListingReportRepository lrr,
                                 ModListingActionRepository mlar,
                                 ModerationIssueRepository mir,
                                 ListingReportBasisRepository lrbr,
                                 ModerationIssueConversionService mics,
                                 ModListingActionConversionService mlacs,
                                 ApplicationEventPublisher eventPublisher) {
        this.glr = groupListingRepository;
        this.glcs = groupListingConversionService;
        this.userRepo = userRepo;
        this.umrr = umrr;
        this.lrr = lrr;
        this.mlar = mlar;
        this.mir = mir;
        this.lrbr = lrbr;
        this.mics = mics;
        this.mlacs = mlacs;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Page<GroupListingResponseDto> modGetAllGroupListings(Pageable pageable) {
        Page<GroupListing> groupListings = glr.findAll(pageable);

        if(groupListings.isEmpty()) {
            log.info("No group listings found.");
        }

        return groupListings.map(glcs::convertListingToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModerationIssueResponseDto> modGetAllIssues(Pageable pageable) {
        Page<ModerationIssue> issues = mir.findAll(pageable);

        return issues.map(mics::convertToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModListingActionDto> getThisWeeksModListingActions(Pageable pageable) {
        Instant week = Instant.now().minus(7, ChronoUnit.DAYS);

        return mlar.findWeeksActions(week, pageable).map(mlacs::convertToDto);
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public ResponseEntity<?> modDeleteListing(ManualModDeleteDto dto, Users mod) {
        try {
            GroupListing groupEntity = glr.findById(dto.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException(dto.getGroupId()));

            prepareManualModRemovalRecords(dto, mod);

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
    public ResponseEntity<?> modClearIssue(ModClearIssueDto dto, Users mod) {
        Map<String, String> response = new HashMap<>();

        try {
            ModerationIssue issue = mir.findById(dto.getIssueId())
                    .orElseThrow(() -> new ResourceNotFoundException(dto.getIssueId()));

            modResetReportCounters(issue);

            recordModeratorAction(issue, dto.getNote(), mod);

            response.put("message", "Report counters reset for issue on groupId: "
                    + issue.getGroupRef().getGroupId());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (Exception e) {
            log.error("modClearIssue failed. Reason: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    private void modResetReportCounters(ModerationIssue issue) {
        issue.setReportTotalCount(0);
        issue.setSpamCount(0);
        issue.setHateSpeechCount(0);
        issue.setNsfwCount(0);
        issue.setScamCount(0);
        issue.setOffTopicCount(0);
        issue.setTrollCount(0);
        issue.setDoxxCount(0);
        issue.setCheatCount(0);
        issue.setOtherCount(0);
        issue.setStatus("Cleared");

        mir.save(issue);
        mir.flush();
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
        ModerationIssue issue = mir.findByGroupRef(condemned)
                .orElseGet(() -> generateModerationIssue(condemned, "Actioned"));

        //max out the count for the report basis provided by the mod
        maxModReportedBasis(issue, dto);

        // archive the listing before deletion
        ListingArchive archive = archiveService.archiveListing(
                condemned, issue, dto.getNote(), mod
        );

        // create a moderator action for deletion
        recordModeratorAction(issue, dto.getNote(), archive, mod);

        // recording the users content moderation history for possibly bans
        updateOrCreateUserModerationRecord(condemned);

        eventPublisher.publishEvent(new ListingModDeleteEvent(condemned, owner));
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

        eventPublisher.publishEvent(new ListingModDeleteEvent(condemned, owner));
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
    public ModerationIssue generateModerationIssue(GroupListing listing, String status) {
        //verify that the ModerationIssue does not already exist
        if(mir.findByGroupRef(listing).isPresent()) {
            throw new IllegalStateException(
                    "ModerationIssue already exists for listing: " + listing.getGroupId()
            );
        }

        ModerationIssue issue = new ModerationIssue(listing, listing.getUsers());
        issue.setStatus(status);
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

    //for auto mod deletions
    @Transactional
    protected void recordModeratorAction(ModerationIssue issue, String note,
                                         ListingArchive archive) {
        ModListingAction modAction = new ModListingAction(issue, note, archive);

        mlar.save(modAction);
        log.info("Moderator action recorded under actionId: {}", modAction.getActionId());
    }

    //for manual moderator actions
    @Transactional
    protected void recordModeratorAction(ModerationIssue issue, String note,
                                         ListingArchive archive, Users mod) {

        ModListingAction modAction = new ModListingAction(issue, note, archive, mod);
        mlar.save(modAction);
        log.info("Manual moderator action recorded under actionId: {}", modAction.getActionId());
    }

    //For manual mod clear issue report counts
    @Transactional
    protected void recordModeratorAction(ModerationIssue issue, String note,
                                         Users mod) {

        ModListingAction modAction = new ModListingAction(issue, note, mod);
        mlar.save(modAction);
        log.info("Moderator action recorded under actionId: {}", modAction.getActionId());
    }

    // maxing out report basis on manual mod deletions to try to use this as a confidence/severity
    // for model training to classify undesirable/unacceptable user generated content.
    @Transactional
    protected void maxModReportedBasis(ModerationIssue issue, ManualModDeleteDto dto) {
        ListingReportBasis modBasis = lrbr.findById(dto.getReportBasis())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ListingReportBasis", dto.getReportBasis())
                );

        switch (modBasis.getBasisLabel()) {
            case "Spam":
                issue.setSpamCount(AUTO_MOD_REPORT_THRESHOLD);
                log.info("A moderator action on issueId: {} has max spam count: {}",
                        issue.getIssueId(), AUTO_MOD_REPORT_THRESHOLD);
                break;
            case "Hate Speech":
                issue.setHateSpeechCount(AUTO_MOD_REPORT_THRESHOLD);
                log.info("A moderator action on issueId: {} has max hateSpeechCount: {}",
                        issue.getIssueId(), AUTO_MOD_REPORT_THRESHOLD);
                break;
            case "NSFW":
                issue.setNsfwCount(AUTO_MOD_REPORT_THRESHOLD);
                log.info("ModerationIssue with issueId: {} has incremented nsfwCount: {}",
                        issue.getIssueId(), AUTO_MOD_REPORT_THRESHOLD);
                break;
            case "Scam/Fraud":
                issue.setScamCount(AUTO_MOD_REPORT_THRESHOLD);
                log.info("ModerationIssue with issueId: {} has incremented scamCount: {}",
                        issue.getIssueId(), AUTO_MOD_REPORT_THRESHOLD);
                break;
            case "Off Topic":
                issue.setOffTopicCount(AUTO_MOD_REPORT_THRESHOLD);
                log.info("ModerationIssue with issueId: {} has incremented offTopicCount: {}",
                        issue.getIssueId(), AUTO_MOD_REPORT_THRESHOLD);
                break;
            case "Low Quality/Troll":
                issue.setTrollCount(AUTO_MOD_REPORT_THRESHOLD);
                log.info("ModerationIssue with issueId: {} has incremented trollCount: {}",
                        issue.getIssueId(), AUTO_MOD_REPORT_THRESHOLD);
                break;
            case "Doxxing/Personal Info":
                issue.setDoxxCount(AUTO_MOD_REPORT_THRESHOLD);
                log.info("ModerationIssue with issueId: {} has incremented doxxCount: {}",
                        issue.getIssueId(), AUTO_MOD_REPORT_THRESHOLD);
                break;
            case "Cheating/RMT":
                issue.setCheatCount(AUTO_MOD_REPORT_THRESHOLD);
                log.info("ModerationIssue with issueId: {} has incremented cheatCount: {}",
                        issue.getIssueId(), AUTO_MOD_REPORT_THRESHOLD);
                break;
            case "Other":
                issue.setOtherCount(AUTO_MOD_REPORT_THRESHOLD);
                log.info("ModerationIssue with issueId: {} has incremented otherCount: {}",
                        issue.getIssueId(), AUTO_MOD_REPORT_THRESHOLD);
                break;
            default:
                log.info("User report was unable to increment the correct ReportBasis count.");
                throw new IllegalStateException("Unexpected value: " + modBasis.getBasisLabel());

        }

        issue.setReportTotalCount(AUTO_MOD_REPORT_THRESHOLD);

        issue.setStatus("Actioned");

        mir.save(issue);
    }
}
