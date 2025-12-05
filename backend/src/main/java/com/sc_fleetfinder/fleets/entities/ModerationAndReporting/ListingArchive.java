package com.sc_fleetfinder.fleets.entities.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name="listing_archive")
@Data
public class ListingArchive {

    protected ListingArchive(){}
    public ListingArchive(GroupListing listing, ModerationIssue issue, String note) {
        this.groupId = listing.getGroupId();
        this.userId = listing.getUsers().getUserId();
        this.username = listing.getUsers().getUsername();
        this.listingTitle = listing.getListingTitle();
        this.listingDescription = listing.getListingDescription();
        this.listingRoles = listing.getAvailableRoles();
        this.listingCommsService = listing.getCommsService();
        this.reportTotalCount = issue.getReportTotalCount();
        this.spamCount = issue.getSpamCount();
        this.hateSpeechCount = issue.getHateSpeechCount();
        this.nsfwCount = issue.getNsfwCount();
        this.scamCount = issue.getScamCount();
        this.offTopicCount = issue.getOffTopicCount();
        this.trollCount = issue.getTrollCount();
        this.doxxCount = issue.getDoxxCount();
        this.cheatCount = issue.getCheatCount();
        this.otherCount = issue.getOtherCount();
        this.status = issue.getStatus();
        this.actionType = "Auto";
        this.actionNote = note;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_archive")
    private Long archiveId;

    @NotNull(message="ListingArchive field 'groupId' cannot be null.")
    @Column(name="id_group")
    private Long groupId;

    @NotNull(message="ListingArchive field 'userId' cannot be null.")
    @Column(name="id_user")
    private Long userId;

    @NotNull(message="ListingArchive field 'username' cannot be null.")
    @Column(name="username")
    private String username;

    @NotNull(message="ListingArchive field 'listingTitle' cannot be null.")
    @Column(name="listing_title")
    private String listingTitle;

    @NotNull(message="ListingArchive field 'listingDescription' cannot be null.")
    @Column(name="listing_description")
    private String listingDescription;

    @Column(name="listing_roles")
    private String listingRoles;

    @Column(name="comms_service")
    private String listingCommsService;

    @NotNull(message="ListingArchive field 'reportsTotal' cannot be null.")
    @Column(name="report_total_count")
    private Integer reportTotalCount;

    @NotNull(message="ListingArchive field 'spamCount' cannot be null.")
    @Column(name="spam_count")
    private Integer spamCount;

    @NotNull(message="ListingArchive field 'hateSpeechCount' cannot be null.")
    @Column(name="hate_speech_count")
    private Integer hateSpeechCount;

    @NotNull(message="ListingArchive field 'nsfwCount' cannot be null.")
    @Column(name="nsfw_count")
    private Integer nsfwCount;

    @NotNull(message="ListingArchive field 'scamCount' cannot be null.")
    @Column(name="scam_count")
    private Integer scamCount;

    @NotNull(message="ListingArchive field 'offTopicCount' cannot be null.")
    @Column(name="off_topic_count")
    private Integer offTopicCount;

    @NotNull(message="ListingArchive field 'trollCount' cannot be null.")
    @Column(name="troll_count")
    private Integer trollCount;

    @NotNull(message="ListingArchive field 'doxxCount' cannot be null.")
    @Column(name="doxx_count")
    private Integer doxxCount;

    @NotNull(message="ListingArchive field 'cheatCount' cannot be null.")
    @Column(name="cheat_count")
    private Integer cheatCount;

    @NotNull(message="ListingArchive field 'otherCount' cannot be null.")
    @Column(name="other_count")
    private Integer otherCount;

    @NotNull(message="ListingArchive field 'otherCount' cannot be null.")
    @Column(name="status")
    private String status;

    @Column(name="id_mod")
    private Long modId;

    @Column(name="modname")
    private String modname;

    @NotNull(message="ListingArchive field 'actionType' cannot be null.")
    @Column(name="action_type")
    private String actionType;

    @Column(name="action_note")
    private String actionNote;

    @Column(name="archive_ts")
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant archiveTs;
}
