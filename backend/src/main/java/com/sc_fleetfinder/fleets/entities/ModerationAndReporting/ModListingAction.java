package com.sc_fleetfinder.fleets.entities.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name="mod_listing_action")
@Getter
@Setter
public class ModListingAction {

    protected ModListingAction() {}

    // for auto mod deletions
    public ModListingAction(ModerationIssue issue, String note, Integer actionBasis,
                            ListingArchive archive) {
        this.archive = archive;
        this.userId = issue.getUserRef().getUserId();
        this.username = issue.getUserRef().getUsername();
        this.actionType = "Auto";
        this.actionBasis.setBasisId(actionBasis);
        this.actionNote = note;
    }

    // for manual mod deletions
    public ModListingAction(ModerationIssue issue, String modNote, Integer actionBasis,
                            ListingArchive archive, Users mod) {
        this.archive = archive;
        this.userId = issue.getUserRef().getUserId();
        this.username = issue.getUserRef().getUsername();
        this.modId = mod.getUserId();
        this.modName = mod.getUsername();
        this.actionType = "Manual";
        this.actionBasis.setBasisId(actionBasis);
        this.actionNote = modNote;
    }

    // for manual mod clear issue report counts
    public ModListingAction(ModerationIssue issue, String modNote, Users mod) {
        this.archive = null;
        this.groupId = issue.getGroupRef().getGroupId();
        this.userId = issue.getUserRef().getUserId();
        this.username = issue.getUserRef().getUsername();
        this.modId = mod.getUserId();
        this.modName = mod.getUsername();
        this.actionType = "Cleared";
        this.actionNote = modNote;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_action")
    private Long actionId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="id_archive", nullable = true)
    private ListingArchive archive;

    @Column(name="id_group", nullable = true)
    private Long groupId;

    @Column(name="id_user", nullable = false)
    @NotNull(message="ModListingAction entity field 'userId' cannot be null.")
    private Long userId;

    @Column(name="username", nullable = false)
    @NotNull(message="ModListingAction entity field 'username' cannot be null.")
    private String username;

    @Column(name="id_mod", nullable = true)
    private Long modId;

    @Column(name="mod_name", nullable = true)
    private String modName;

    @Column(name="action_type")
    private String actionType;

    @Column(name="action_note")
    private String actionNote;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="action_basis", nullable = false)
    private ListingReportBasis actionBasis;

    @Column(name="action_ts")
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant actionTs;
}
