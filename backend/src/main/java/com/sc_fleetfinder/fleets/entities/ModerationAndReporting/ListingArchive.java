package com.sc_fleetfinder.fleets.entities.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameEnvironment;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameExperience;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplayCategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GameplaySubcategory;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.GroupStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.Legality;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetMoonSystem;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlanetarySystem;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PlayStyle;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.PvpStatus;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

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
        this.server = listing.getServer();
        this.environment = listing.getEnvironment();
        this.experience = listing.getExperience();
        this.playStyle = listing.getPlayStyle();
        this.legality = listing.getLegality();
        this.groupStatus = listing.getGroupStatus();
        this.eventSchedule = listing.getEventSchedule();
        this.category = listing.getCategory();
        this.subcategory = listing.getSubcategory();
        this.pvpStatus = listing.getPvpStatus();
        this.system = listing.getSystem();
        this.planetMoonSystem = listing.getPlanetMoonSystem();
        this.currentPartySize = listing.getCurrentPartySize();
        this.desiredPartySize = listing.getDesiredPartySize();
        this.commsOption = listing.getCommsOption();
        this.listingCreationTs = listing.getCreationTimestamp();
        this.listingLastUpdated = listing.getLastUpdated();
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

    @ManyToOne
    @JoinColumn(name="server_id")
    @NotNull(message = "GroupListing entity field 'server' cannot be null")
    private ServerRegion server;

    @ManyToOne
    @JoinColumn(name="environment_id")
    @NotNull(message = "GroupListing entity field 'environment' cannot be null")
    private GameEnvironment environment;

    @ManyToOne
    @JoinColumn(name="experience_id")
    @NotNull(message = "GroupListing entity field 'experience' cannot be null")
    private GameExperience experience;

    @ManyToOne
    @Nullable
    @JoinColumn(name="style_id")
    private PlayStyle playStyle;

    @ManyToOne
    @JoinColumn(name="legality_id")
    @NotNull(message = "GroupListing entity field 'legality' cannot be null")
    private Legality legality;

    @ManyToOne
    @JoinColumn(name="group_status_id")
    @NotNull(message = "GroupListing entity field 'groupStatus' cannot be null")
    private GroupStatus groupStatus;

    @Column(name="event_schedule")
    @Nullable
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant eventSchedule;

    @ManyToOne
    @JoinColumn(name="category_id")
    @NotNull(message = "GroupListing category cannot be null")
    private GameplayCategory category;

    @ManyToOne
    @JoinColumn(name="subcategory_id")
    @Nullable
    private GameplaySubcategory subcategory;

    @ManyToOne
    @JoinColumn(name="pvp_status_id")
    @NotNull(message = "GroupListing entity field 'pvpStatus' cannot be null")
    private PvpStatus pvpStatus;

    @ManyToOne
    @JoinColumn(name="system_id")
    @NotNull(message = "GroupListing entity field 'system' cannot be null")
    private PlanetarySystem system;

    @ManyToOne
    @JoinColumn(name="planet_id")
    @Nullable
    private PlanetMoonSystem planetMoonSystem;

    @Column(name="current_party_size")
    @NotNull(message="ListingArchive field 'currentPartySize' cannot be null.")
    private Integer currentPartySize;

    @Column(name="desired_party_size")
    @NotNull(message="ListingArchive field 'desiredPartySize' cannot be null.")
    private Integer desiredPartySize;

    @Column(name="comms_options")
    @NotNull(message = "GroupListing entity field 'commsOptions' cannot be null")
    private String commsOption;

    @Column(name="listing_creation_ts")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message="ListingArchive field 'listingCreationTs' cannot be null.")
    private Instant listingCreationTs;

    @Column(name="listing_last_updated")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message="ListingArchive field 'listingLastUpdated' cannot be null.")
    private Instant listingLastUpdated;

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
