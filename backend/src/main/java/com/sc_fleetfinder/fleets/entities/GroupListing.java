package com.sc_fleetfinder.fleets.entities;

import com.sc_fleetfinder.fleets.util.CommsOption;
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
    private int groupId;

    @ManyToOne
    @JoinColumn(name="id_user", nullable = false)
    private User user;

    @Column(name="server_id")
    private int serverId;

    @Column(name="environment_id")
    private int environmentId;

    @Column(name="experience_id")
    private int experienceId;

    @Column(name="listing_title")
    private String listingTitle;

    @Column(name="listing_user")
    private String listingUser;

    @Column(name="style_id")
    private int styleId;

    @Column(name="legality_id")
    private int legality;

    @Column(name="group_status_id")
    private int groupStatus;

    @Column(name="event_schedule")
    private LocalDateTime eventSchedule;

    @Column(name="category_id")
    private int categoryId;

    @Column(name="subcategory_id")
    private int subcategoryId;

    @Column(name="pvp_status_id")
    private int pvpStatusId;

    @Column(name="system_id")
    private int systemId;

    @Column(name="planet_id")
    private int planetId;

    @Column(name="activity_description")
    private String activityDescription;

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
