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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ListingReportingServiceImpl implements ListingReportingService {

    private final GroupListingRepository glr;
    private final ModerationIssueRepository mir;
    private final ListingReportBasisRepository lbr;
    private final ListingReportRepository lrr;

    public ListingReportingServiceImpl(GroupListingRepository glr, ModerationIssueRepository mir,
                                       ListingReportBasisRepository lbr, ListingReportRepository lrr) {
        this.glr = glr;
        this.mir = mir;
        this.lbr = lbr;
        this.lrr = lrr;
    }

    @Override
    public ResponseEntity<?> generateListingReport(SubmitListingReportDto dto) {
        //Get the reported listing
        GroupListing reported = glr.findById(dto.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("GroupListing", dto.getGroupId()));

        //check if the listing has already been reported, i.e. has a ModerationIssue
        //if there is not an existing ModerationIssue for this listing, then create a new one
        ModerationIssue modIssue = mir.findByGroupRef(reported)
                .orElseGet(() -> generateModerationIssue(reported));

        ListingReportBasis basis = lbr.findById(dto.getReportBasis())
                .orElseThrow(() -> new ResourceNotFoundException("ListingReportBasis", dto.getReportBasis()));

        //Use a required fields constructor to generate the ListingReport
        ListingReport report = new ListingReport(modIssue, reported, dto.getUser(), basis);
        lrr.save(report);

        Map<String, Long> response = new HashMap<>();
        response.put("reportId", report.getReportId());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ModerationIssue generateModerationIssue(GroupListing listing) {
        //verify that the ModerationIssue does not already exist
        if(mir.findByGroupRef(listing).isPresent()) {
            throw new IllegalStateException(
                    "ModerationIssue already exists for listing: " + listing.getGroupId()
            );
        }

        ModerationIssue issue = new ModerationIssue(listing, listing.getUsers());
        mir.save(issue);

        return issue;
    }

    @Override
    public ResponseEntity<?> getUsersReportBrief(Users user) {
        Set<Long> brief = lrr.findByReportingUserRef(user).stream()
                .map(report -> report.getListingRef().getGroupId())
                .collect(Collectors.toSet());

        return ResponseEntity.status(HttpStatus.OK).body(brief);
    }
}
