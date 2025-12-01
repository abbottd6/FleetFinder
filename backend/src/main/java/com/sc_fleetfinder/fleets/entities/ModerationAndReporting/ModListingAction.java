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
@Table(name="moderator_listing_actions")
@Getter
@Setter
public class ModListingAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_action")
    private Long actionId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="id_group", nullable = false)
    @NotNull(message="ModListingAction entity field 'listingRef' cannot be null.")
    private GroupListing listingRef;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="id_user", nullable = false)
    @NotNull(message="ModListingAction entity field 'userRef' cannot be null.")
    private Users userRef;

    @ManyToOne
    @JoinColumn(name="id_mod", nullable = false)
    @NotNull(message="ModListingAction entity field 'modRef' cannot be null.")
    private Users modRef;

    @Column(name="action_type")
    private String actionType;

    @Column(name="action_note")
    private String actionNote;

    @Column(name="action_ts")
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant actionTs;
}
