package com.sc_fleetfinder.fleets.services.reporting_services;

import com.sc_fleetfinder.fleets.DAO.GroupListingRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingReportBasisRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ListingReportRepository;
import com.sc_fleetfinder.fleets.DAO.ModerationAndReporting.ModerationIssueRepository;
import com.sc_fleetfinder.fleets.DTO.requestDTOs.ModerationAndReporting.SubmitListingReportDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReport;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ListingReportBasis;
import com.sc_fleetfinder.fleets.entities.ModerationAndReporting.ModerationIssue;
import com.sc_fleetfinder.fleets.entities.Users;
import com.sc_fleetfinder.fleets.exceptions.ResourceNotFoundException;
import com.sc_fleetfinder.fleets.services.mod_services.ModerationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ListingReportingServiceImpl implements ListingReportingService {
    private final int AUTO_MOD_REPORT_THRESHOLD = 5;

    @Autowired
    private ModerationService modService;

    private final GroupListingRepository glr;
    private final ModerationIssueRepository mir;
    private final ListingReportBasisRepository lbr;
    private final ListingReportRepository lrr;

    @PersistenceContext
    private EntityManager em;

    public ListingReportingServiceImpl(GroupListingRepository groupListingRepository,
                                       ModerationIssueRepository moderationIssueRepository,
                                       ListingReportBasisRepository listingReportBasisRepository,
                                       ListingReportRepository listingReportRepository) {
        this.glr = groupListingRepository;
        this.mir = moderationIssueRepository;
        this.lbr = listingReportBasisRepository;
        this.lrr = listingReportRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<?> generateListingReport(SubmitListingReportDto dto, Users reporterUser) {
        //Get the reported listing
        GroupListing reported = glr.findById(dto.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("GroupListing", dto.getGroupId()));

        //check if the listing has already been reported, i.e. has a ModerationIssue
        //if there is not an existing ModerationIssue for this listing, then create a new one
        ModerationIssue modIssue = mir.findByGroupRef(reported)
                .orElseGet(() -> modService.generateModerationIssue(reported));

        ListingReportBasis basis = lbr.findById(dto.getReportBasis())
                .orElseThrow(() -> new ResourceNotFoundException("ListingReportBasis", dto.getReportBasis()));

        //Use a required fields constructor to generate the ListingReport
        ListingReport report = new ListingReport(modIssue, reported, reporterUser, basis);
        lrr.save(report);

        // increment the modIssue report category corresponding to this report
        // and set the total reports count to the number of ListingReports associated
        // with this ModerationIssue
        incrementOnReport(modIssue, basis);

        // Check ModerationIssue against AUTO_MOD_REPORT_THRESHOLD to determine if
        // auto mod delete is warranted
        checkAutoModThresh(modIssue);

        Map<String, String> response = new HashMap<>();
        response.put("Report Status:", "Success");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Transactional
    protected void incrementOnReport(ModerationIssue issue, ListingReportBasis basis) {
        switch (basis.getBasisLabel()) {
            case "Spam":
                int spamCount = issue.getSpamCount();
                ++spamCount;
                issue.setSpamCount(spamCount);
                log.info("ModerationIssue with issueId: {} has incremented spamCount: {}",
                        issue.getIssueId(), spamCount);
                break;
            case "Hate Speech":
                int hateSpeechCount = issue.getHateSpeechCount();
                ++hateSpeechCount;
                issue.setHateSpeechCount(hateSpeechCount);
                log.info("ModerationIssue with issueId: {} has incremented hateSpeechCount: {}",
                        issue.getIssueId(), hateSpeechCount);
                break;
            case "NSFW":
                int nsfwCount = issue.getNsfwCount();
                ++nsfwCount;
                issue.setNsfwCount(nsfwCount);
                log.info("ModerationIssue with issueId: {} has incremented nsfwCount: {}",
                        issue.getIssueId(), nsfwCount);
                break;
            case "Scam/Fraud":
                int scamCount = issue.getScamCount();
                ++scamCount;
                issue.setScamCount(scamCount);
                log.info("ModerationIssue with issueId: {} has incremented scamCount: {}",
                        issue.getIssueId(), scamCount);
                break;
            case "Off Topic":
                int offTopicCount = issue.getOffTopicCount();
                ++offTopicCount;
                issue.setOffTopicCount(offTopicCount);
                log.info("ModerationIssue with issueId: {} has incremented offTopicCount: {}",
                        issue.getIssueId(), offTopicCount);
                break;
            case "Low Quality/Troll":
                int trollCount = issue.getTrollCount();
                ++trollCount;
                issue.setTrollCount(trollCount);
                log.info("ModerationIssue with issueId: {} has incremented trollCount: {}",
                        issue.getIssueId(), trollCount);
                break;
            case "Doxxing/Personal Info":
                int doxxCount = issue.getDoxxCount();
                ++doxxCount;
                issue.setDoxxCount(doxxCount);
                log.info("ModerationIssue with issueId: {} has incremented doxxCount: {}",
                        issue.getIssueId(), doxxCount);
                break;
            case "Cheating/RMT":
                int cheatCount = issue.getCheatCount();
                ++cheatCount;
                issue.setCheatCount(cheatCount);
                log.info("ModerationIssue with issueId: {} has incremented cheatCount: {}",
                        issue.getIssueId(), cheatCount);
                break;
            case "Other":
                int otherCount = issue.getOtherCount();
                ++otherCount;
                issue.setOtherCount(otherCount);
                log.info("ModerationIssue with issueId: {} has incremented otherCount: {}",
                        issue.getIssueId(), otherCount);
                break;
            default:
                log.info("User report was unable to increment the correct ReportBasis count.");
                throw new IllegalStateException("Unexpected value: " + basis.getBasisLabel());

        }

        issue.setReportTotalCount(lrr.findByModIssueRef(issue).size());

        mir.save(issue);
    }

    @Transactional
    protected void checkAutoModThresh(ModerationIssue issue) {
        if(issue.getReportTotalCount() >= AUTO_MOD_REPORT_THRESHOLD) {
            issue.setStatus("AutoMod");

            mir.save(issue);
            modService.prepareAutoModRemovalRecords(issue);
        }
    }

    @Override
    public ResponseEntity<?> getUsersReportBrief(Users user) {
        Set<Long> brief = lrr.findByReportingUserRef(user).stream()
                .map(report -> report.getListingRef().getGroupId())
                .collect(Collectors.toSet());

        return ResponseEntity.status(HttpStatus.OK).body(brief);
    }


    // debugging jpa transient instance errors
    private void dumpHibernateState(String label) {
        SessionImplementor si = em.unwrap(SessionImplementor.class);
        org.hibernate.engine.spi.PersistenceContext pc = si.getPersistenceContext();

        System.out.println("=== Hibernate state dump: " + label + " ===");

        Map.Entry<Object, EntityEntry>[] entries = pc.reentrantSafeEntityEntries();

        for (Map.Entry<Object, EntityEntry> e : entries) {
            Object entity = e.getKey();       // the actual entity instance
            EntityEntry entry = e.getValue(); // Hibernate metadata

            System.out.println("Entity: " + entity + ", status=" + entry.getStatus());

            // GroupListing-specific logging
            if (entity instanceof GroupListing gl) {
                Long id = gl.getGroupId();
                System.out.println("GroupListing entity: " + gl
                        + " id=" + id
                        + " managed=" + si.contains(gl));
            }

            // ListingReport-specific logging
            if (entity instanceof ListingReport lr) {
                GroupListing ref = lr.getListingRef();
                Long refId = (ref != null ? ref.getGroupId() : null);
                System.out.println("ListingReport entity: " + lr
                        + " id=" + lr.getReportId()
                        + " listingRef=" + ref
                        + " listingRef.id=" + refId
                        + " listingRefManaged=" + (ref != null && si.contains(ref)));
            }
        }

        System.out.println("=== end state dump ===");
    }
}
