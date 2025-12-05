package com.sc_fleetfinder.fleets.entities.ModerationAndReporting;

import com.sc_fleetfinder.fleets.entities.GroupListing;
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
    public ModListingAction(ModerationIssue issue, String note, ListingArchive archive) {
        this.archive = archive;
        this.userId = issue.getUserRef().getUserId();
        this.username = issue.getUserRef().getUsername();
        this.actionType = "Auto";
        this.actionNote = note;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_action")
    private Long actionId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="id_archive")
    private ListingArchive archive;

    @Column(name="id_user", nullable = false)
    @NotNull(message="ModListingAction entity field 'userId' cannot be null.")
    private Long userId;

    @Column(name="username", nullable = false)
    @NotNull(message="ModListingAction entity field 'username' cannot be null.")
    private String username;

    @Column(name="id_mod", nullable = false)
    private Long modId;

    @Column(name="mod_name")
    private String modName;

    @Column(name="action_type")
    private String actionType;

    @Column(name="action_note")
    private String actionNote;

    @Column(name="action_ts")
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant actionTs;
}
