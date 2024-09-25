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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "groupId cannot be null")
    private Long groupId;

    @ManyToOne
    @JoinColumn(name="id_user", nullable = false)
    @NotNull(message = "GroupListing user cannot be null")
    private User user;

    @ManyToOne
    @JoinColumn(name="server_id")
    @NotNull(message = "GroupListing server cannot be null")
    private ServerRegion server;

    @ManyToOne
    @JoinColumn(name="environment_id")
    @NotNull(message = "GroupListing environment cannot be null")
    private GameEnvironment environment;

    @ManyToOne
    @JoinColumn(name="experience_id")
    @NotNull(message = "GroupListing experience cannot be null")
    private GameExperience experience;

    @Column(name="listing_title")
    @NotBlank(message = "GroupListing Title cannot be blank")
    @Size(min = 2, max = 65, message = "Listing title must be between 2 and 65 characters")
    private String listingTitle;

    @ManyToOne
    @JoinColumn(name="style_id")
    private PlayStyle playStyle;

    @ManyToOne
    @JoinColumn(name="legality_id")
    @NotNull(message = "GroupListing legality cannot be null")
    private Legality legality;

    @ManyToOne
    @JoinColumn(name="group_status_id")
    @NotNull(message = "GroupListing groupStatus cannot be null")
    private GroupStatus groupStatus;

    @Column(name="event_schedule")
    @NotNull(message = "GroupListing eventSchedule cannot be null")
    private LocalDateTime eventSchedule;

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
    @NotNull(message = "GroupListing pvpStatus cannot be null")
    private PvpStatus pvpStatus;

    @ManyToOne
    @JoinColumn(name="system_id")
    private PlanetarySystem system;

    @ManyToOne
    @JoinColumn(name="planet_id")
    private PlanetMoonSystem planetMoonSystem;

    @Column(name="listing_description")
    @NotNull(message = "GroupListing description cannot be null")
    @Size(min = 0, max = 500, message = "Listing description cannot be longer than 400 characters")
    private String listingDescription;

    @Column(name="desired_party_size")
    @Min(value = 2, message = "Desired party size must be at least 2.")
    @Max(value = 1000, message = "Desired party size cannot exceed 1,000.")
    private int desiredPartySize;

    @Column(name="available_roles")
    private String availableRoles;

    @Enumerated(EnumType.STRING)
    @Column(name="comms_options")
    @NotNull(message = "GroupListing commsOptions cannot be null")
    private CommsOption commsOption;

    @Column(name="comms_service")
    private String commsService;

    @CreationTimestamp
    @Column(name="creation_timestamp")
    @NotNull(message = "GroupListing creationTimestamp cannot be null")
    private LocalDateTime creationTimestamp;

    @UpdateTimestamp
    @Column(name="last_updated")
    private LocalDateTime lastUpdated;
}
