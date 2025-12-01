package com.sc_fleetfinder.fleets.entities.ModerationAndReporting;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name="listing_report")
@Getter
@Setter
public class ListingReport {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id_report")
    private Long reportId;

    @ManyToOne
    @JoinColumn(name="id_issue", nullable = false)
    @NotNull(message="ListingReport entity field 'modIssueRef' cannot be null.")
    @JsonBackReference
    private ModerationIssue modIssueRef;

    @ManyToOne
    @JoinColumn(name="id_group", nullable = false)
    @NotNull(message="ListingReport entity field 'listingRef' cannot be null.")
    private GroupListing listingRef;

    @ManyToOne
    @JoinColumn(name="id_user", nullable = false)
    @NotNull(message="ListingReport entity field 'reportingUserRef' cannot be null.")
    private Users reportingUserRef;

    @ManyToOne
    @JoinColumn(name="id_basis")
    @NotNull(message="ListingReport entity field 'reportBasisObj' cannot be null.")
    private ListingReportBasis reportBasisObj;

    @Column(name="created_at")
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;

    @Column(name="status")
    private String status;
}
