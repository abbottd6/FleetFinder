package entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import util.GroupStatus;
import util.Legality;
import util.PvpStatus;

import java.time.LocalDateTime;

@Entity
@Table(name="group_listing")
@Data
public class GroupListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_group")
    private int groupId;

    @Column(name="id_user")
    private int userId;

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

    @Enumerated(EnumType.STRING)
    @Column(name="legality")
    private Legality legality;

    @Enumerated(EnumType.STRING)
    @Column(name="group_status")
    private GroupStatus groupStatus;

    @Column(name="event_schedule")
    private LocalDateTime eventSchedule;

    @Column(name="category_id")
    private int categoryId;

    @Column(name="subcategory_id")
    private int subcategoryId;

    @Enumerated(EnumType.STRING)
    @Column(name="pvp_status")
    private PvpStatus pvpStatus;

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
    private String commsOptions;

    @Column(name="comms_service")
    private String commsService;

    @CreationTimestamp
    @UpdateTimestamp
    @Column(name="creation_timestamp")
    private LocalDateTime creationTimestamp;
}
