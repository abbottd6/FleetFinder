package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.utils.GroupManagement.GroupMemberId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Entity
@Table(name="mgmt_crew_position")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CrewPosition {

    public CrewPosition(GroupListing listing,
                        Long rootSubgroupId,
                        GroupManagementSubgroup subgroup,
                        CrewPositionTemplate template) {
        this.groupListing = listing;
        this.rootSubgroupId = rootSubgroupId;
        this.subgroup = subgroup;
        this.sortOrder = template.getSortOrder();
        this.positionRole = template.getPositionRole();
        this.positionNote = template.getPositionNotes();
    }

    public CrewPosition(GroupListing listing) {
        this.groupListing = listing;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_position")
    private Long positionId;

    @ManyToOne
    @JoinColumn(name="listing_id", referencedColumnName="id_group", nullable=false)
    @NotNull(message="CrewPosition entity field 'groupListing' cannot be null.")
    private GroupListing groupListing;

    @ManyToOne
    @JoinColumn(name="subgroup_id", referencedColumnName="id_subgroup", nullable = false)
    private GroupManagementSubgroup subgroup;

    @Column(name="root_subgroup_id", nullable = true)
    private Long rootSubgroupId;

    @Generated(event = EventType.INSERT)
    @Column(name="sort_order", nullable = true, columnDefinition="TINYINT NULL")
    private Integer sortOrder;

    @ManyToOne
    @JoinColumn(name="position_role_id", referencedColumnName="id_role", nullable = true)
    private CrewRoleClassification positionRole;

    @Column(name="position_note", columnDefinition = "VARCHAR(128) NULL")
    private String positionNote;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "assigned_member_id", referencedColumnName = "user_id", insertable = false, updatable = false),
            @JoinColumn(name = "listing_id", referencedColumnName = "listing_id", insertable=false, updatable=false)
    })
    private GroupMember assignedMember;

    @Column(name="assigned_member_id")
    private Long assignedMemberUserId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="deleted_at", nullable = true)
    private Instant deletedAt;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="created_at")
    private Instant createdAt;
}
