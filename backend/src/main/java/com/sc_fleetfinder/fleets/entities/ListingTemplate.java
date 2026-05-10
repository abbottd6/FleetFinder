package com.sc_fleetfinder.fleets.entities;

import com.sc_fleetfinder.fleets.utils.LanguageOptions;
import com.sc_fleetfinder.fleets.utils.ListingDiscoveryOptions;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.sc_fleetfinder.fleets.entities.ListingReferenceDataEntities.ServerRegion;
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
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;


@Entity
@Table(name="listing_template")
@Getter
@Setter
public class ListingTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_template")
    private Long templateId;

    @ManyToOne
    @JoinColumn(name="id_user", nullable = false)
    private Users user;

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
    @Size(max = 128, message = "ListingTemplate entity field 'listingTitle' must be less than 128 characters")
    private String listingTitle;

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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant eventSchedule;

    //TODO: MODIFY THIS SO USERS CAN SELECT MULTIPLE CATEGORIES PER LSITING
    // CREATE A 'listing_category' TABLE THAT RELATES A LISTING TO A CATEGORY SPECIFICATION
    // WHERE EACH listing_category ENTRY CONTAINS id_group AND A SINGLE id_category
    // SO, MULTIPLE ENTRIES PER LISTING IF THERE ARE MULTIPLE CATEGORIES
    @ManyToOne
    @JoinColumn(name="category_id")
    private GameplayCategory category;

    //TODO: SAME AS CATEGORY
    // NEED TO FIGURE OUT HOW THIS WILL WORK FOR ARCHIVING AND TEMPLATING
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

    @Column(name="listing_description", nullable = false)
    @NotNull(message = "ListingTemplate field 'listingDescription' cannot be null.")
    @Size(max=2000, message="ListingTemplate field 'listingDescription' must be less than 2000 characters.")
    private String listingDescription;

    @Column(name="desired_party_size")
    @Max(value=100, message="ListingTemplate field 'desiredPartySize' must be <= 100.")
    private Integer desiredPartySize;

    @Column(name="current_party_size")
    @Max(value=100, message="ListingTemplate field 'currentPartySize' must be <= 100.")
    private Integer currentPartySize;

    @Column(name="available_roles")
    @Size(max=255, message="ListingTemplate field 'availableRoles' must be less than 255 characters.")
    private String availableRoles;

    @Column(name="comms_options")
    private String commsOption;

    @Column(name="comms_service")
    private String commsService;

    @Enumerated(EnumType.STRING)
    @Column(name="language_code")
    @NotNull(message="ListingTemplate field 'languageCode' cannot be null.")
    private LanguageOptions languageCode;

    @Column(name="join_request_prompt", columnDefinition = "VARCHAR(512) NULL", nullable = true)
    private String joinRequestPrompt;

    @Enumerated(EnumType.STRING)
    @Column(name="discovery", nullable = true)
    private ListingDiscoveryOptions discovery;

    @CreationTimestamp
    @Column(name="creation_timestamp")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant creationTimestamp;
}
