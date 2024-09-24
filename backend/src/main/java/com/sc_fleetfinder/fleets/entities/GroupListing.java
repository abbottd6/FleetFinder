package com.sc_fleetfinder.fleets.entities;

import com.sc_fleetfinder.fleets.util.CommsOption;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="group_listing")
@Getter
@Setter
public class GroupListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_group")
    private Long groupId;

    @ManyToOne
    @JoinColumn(name="id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="server_id")
    private ServerRegion server;

    @ManyToOne
    @JoinColumn(name="environment_id")
    private GameEnvironment environment;

    @ManyToOne
    @JoinColumn(name="experience_id")
    private GameExperience experience;

    @Column(name="listing_title")
    private String listingTitle;

    //Don't think I need this
    @Column(name="listing_user")
    private String listingUser;

    @ManyToOne
    @JoinColumn(name="style_id")
    private PlayStyle playStyle;

    @ManyToOne
    @JoinColumn(name="legality_id")
    private Legality legality;

    @ManyToOne
    @JoinColumn(name="group_status_id")
    private GroupStatus groupStatus;

    @Column(name="event_schedule")
    private LocalDateTime eventSchedule;

    @ManyToOne
    @JoinColumn(name="category_id")
    private GameplayCategory category;

    @ManyToOne
    @JoinColumn(name="subcategory_id")
    private GameplaySubcategory subcategory;

    @ManyToOne
    @JoinColumn(name="pvp_status_id")
    private PvpStatus pvpStatus;

    @ManyToOne
    @JoinColumn(name="system_id")
    private PlanetarySystem system;

    @ManyToOne
    @JoinColumn(name="planet_id")
    private PlanetMoonSystem planetMoonSystem;

    @Column(name="listing_description")
    private String listingDescription;

    @Column(name="desired_party_size")
    private int desiredPartySize;

    @Column(name="available_roles")
    private String availableRoles;

    @Enumerated(EnumType.STRING)
    @Column(name="comms_options")
    private CommsOption commsOption;

    @Column(name="comms_service")
    private String commsService;

    @CreationTimestamp
    @Column(name="creation_timestamp")
    private LocalDateTime creationTimestamp;

    @UpdateTimestamp
    @Column(name="last_updated")
    private LocalDateTime lastUpdated;
}
