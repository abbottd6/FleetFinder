package com.sc_fleetfinder.fleets.entities.ModerationAndReporting;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="moderation_issue")
@Getter
@Setter
public class ModerationIssue {

    public ModerationIssue() {};
    public ModerationIssue(GroupListing listing, Users userRef) {
        this.groupRef = listing;
        this.userRef = userRef;
        this.reportTotalCount = 0;
        this.spamCount = 0;
        this.hateSpeechCount = 0;
        this.nsfwCount = 0;
        this.scamCount = 0;
        this.offTopicCount = 0;
        this.trollCount = 0;
        this.doxxCount = 0;
        this.cheatCount = 0;
        this.otherCount = 0;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_issue")
    private Long issueId;

    @OneToOne
    @JoinColumn(name="id_group", nullable = false)
    @NotNull(message="ModerationIssue entity field 'listingRef' cannot be null.")
    private GroupListing groupRef;

    @ManyToOne
    @JoinColumn(name="id_user", nullable = false)
    @NotNull(message="ModerationIssue entity field 'userRef' cannot be null.")
    private Users userRef;

    @Column(name="report_total_count")
    @NotNull(message="ModerationIssue entity field 'reportTotalCount' cannot be null.")
    private Integer reportTotalCount;

    @Column(name="spam_count")
    @NotNull(message="ModerationIssue entity field 'spamCount' cannot be null.")
    private Integer spamCount;

    @Column(name="hate_speech_count")
    @NotNull(message="ModerationIssue entity field 'hateSpeechCount' cannot be null.")
    private Integer hateSpeechCount;

    @Column(name="nsfw_count")
    @NotNull(message="ModerationIssue entity field 'nsfwCount' cannot be null.")
    private Integer nsfwCount;

    @Column(name="scam_count")
    @NotNull(message="ModerationIssue entity field 'scamCount' cannot be null.")
    private Integer scamCount;

    @Column(name="off_topic_count")
    @NotNull(message="ModerationIssue entity field 'offTopicCount' cannot be null.")
    private Integer offTopicCount;

    @Column(name="troll_count")
    @NotNull(message="ModerationIssue entity field 'trollCount' cannot be null.")
    private Integer trollCount;

    @Column(name="doxx_count")
    @NotNull(message="ModerationIssue entity field 'doxxCount' cannot be null.")
    private Integer doxxCount;

    @Column(name="cheat_count")
    @NotNull(message="ModerationIssue entity field 'cheatcount' cannot be null.")
    private Integer cheatCount;

    @Column(name="other_count")
    @NotNull(message="ModerationIssue entity field 'otherCount' cannot be null.")
    private Integer otherCount;

    @Column(name="first_report_ts")
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant firstReportTs;

    @Column(name="last_report_ts")
    @UpdateTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastReportTs;

    @Column(name="status")
    private String status;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="modIssueRef", fetch= FetchType.LAZY)
    @JsonManagedReference
    private Set<ListingReport> reports = new HashSet<>();
}
