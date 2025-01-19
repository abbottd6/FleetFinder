package com.sc_fleetfinder.fleets.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

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
    @NotNull(message = "GroupListing entity field 'user' cannot be null")
    @JsonBackReference
    private User user;

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

    @Column(name="listing_title")
    @NotBlank(message = "GroupListing entity field 'listingTitle' cannot be blank")
    @Size(min = 2, max = 65, message = "GroupListing entity field 'listingTitle' must be between 2 and 65 characters")
    private String listingTitle;

    @ManyToOne
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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime eventSchedule;

    @ManyToOne
    @JoinColumn(name="category_id")
    @NotNull(message = "GroupListing category cannot be null")
    private GameplayCategory category;

    @ManyToOne
    @JoinColumn(name="subcategory_id")
    @NotNull(message = "GroupListing subcategory cannot be null")
    private GameplaySubcategory subcategory;

    @ManyToOne
    @JoinColumn(name="pvp_status_id")
    @NotNull(message = "GroupListing entity field 'pvpStatus' cannot be null")
    private PvpStatus pvpStatus;

    @ManyToOne
    @JoinColumn(name="system_id")
    private PlanetarySystem system;

    @ManyToOne
    @JoinColumn(name="planet_id")
    private PlanetMoonSystem planetMoonSystem;

    @Column(name="listing_description")
    @NotBlank(message = "GroupListing entity field 'listingDescription' cannot be blank")
    @Size(max = 500, message = "Listing description cannot be longer than 500 characters")
    private String listingDescription;

    @Column(name="desired_party_size")
    @Min(value = 2, message = "GroupListing entity field 'desiredPartySize' must be at least 2.")
    @Max(value = 1000, message = "GroupListing entity field 'desiredPartySize' cannot exceed 1,000.")
    private int desiredPartySize;

    @Column(name="current_party_size")
    @Min(value = 1, message = "GroupListing entity field 'currentPartySize' must be at least 1.")
    @Max(value = 1000, message = "GroupListing entity field 'currentPartySize' cannot exceed 1,000.")
    private int currentPartySize;

    @Column(name="available_roles")
    private String availableRoles;

    @Column(name="comms_options")
    @NotNull(message = "GroupListing entity field 'commsOptions' cannot be null")
    private String commsOption;

    @Column(name="comms_service")
    private String commsService;

    @CreationTimestamp
    @Column(name="creation_timestamp")
    private LocalDateTime creationTimestamp;

    @UpdateTimestamp
    @Column(name="last_updated")
    private LocalDateTime lastUpdated;
}
