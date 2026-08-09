package com.sc_fleetfinder.fleets.entities.GroupManagement;

import com.sc_fleetfinder.fleets.DTO.requestDTOs.GroupManagement.AddNewSubgroupRequestDto;
import com.sc_fleetfinder.fleets.DTO.responseDTOs.GroupManagement.GroupCompositionSubgroupDto;
import com.sc_fleetfinder.fleets.entities.GroupListing;
import com.sc_fleetfinder.fleets.utils.GroupManagement.SubgroupDropListOrientation;
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
@Table(name="group_management_subgroup")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupManagementSubgroup {

    public GroupManagementSubgroup(GroupListing listing,
                                   Long rootSubgroupId,
                                   GroupManagementSubgroup parentSubgroup,
                                   CrewSubgroupTemplate template) {
        this.groupListing = listing;
        this.rootSubgroupId = rootSubgroupId;
        this.parentSubgroup = parentSubgroup;
        this.subgroupLabel = template.getSubgroupLabel();
        this.subgroupNotes = template.getSubgroupNotes();
        this.dropListOrientation = template.getDropListOrientation();
    }

    public GroupManagementSubgroup(GroupCompositionSubgroupDto subDto,
                                   GroupListing listing,
                                   GroupManagementSubgroup parentSubgroup) {
        this.groupListing = listing;
        this.rootSubgroupId = subDto.getRootSubgroupId();
        this.parentSubgroup = parentSubgroup;
        this.subgroupLabel = subDto.getSubgroupLabel();
        this.subgroupNotes = subDto.getSubgroupNotes();
        this.sortOrder = subDto.getSortOrder();
        this.dropListOrientation = subDto.getDropListOrientation();
    }

    public GroupManagementSubgroup(AddNewSubgroupRequestDto requestDto, GroupListing listing, GroupManagementSubgroup parentSubgroup) {
        this.groupListing = listing;
        this.rootSubgroupId = requestDto.getRootSubgroupId();
        this.parentSubgroup = parentSubgroup;
        this.subgroupLabel = requestDto.getSubgroupLabel();
        this.subgroupNotes = requestDto.getSubgroupNotes();
        this.dropListOrientation = requestDto.getDropListOrientation();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_subgroup")
    private Long subgroupId;

    @ManyToOne
    @JoinColumn(name="listing_id", referencedColumnName="id_group", nullable = false)
    @NotNull(message="GroupManagementSubgroup entity field groupListing cannot be null.")
    private GroupListing groupListing;

    @Column(name="root_subgroup_id", nullable = true)
    private Long rootSubgroupId;

    @ManyToOne
    @JoinColumn(name="parent_subgroup_id", referencedColumnName="id_subgroup", nullable = true)
    private GroupManagementSubgroup parentSubgroup;

    @Column(name="subgroup_label", nullable = false, columnDefinition="VARCHAR(64) NULL")
    @NotNull(message="GroupManagementSubgroup entity field subgroupLabel cannot be null.")
    private String subgroupLabel;

    @Column(name="subgroup_notes", nullable = true)
    private String subgroupNotes;

    @Generated(event = EventType.INSERT)
    @Column(name="sort_order", nullable = true, columnDefinition="TINYINT NULL")
    private Integer sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(name="drop_list_orientation", nullable = false)
    private SubgroupDropListOrientation dropListOrientation;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="deleted_at", nullable = true)
    private Instant deletedAt;

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name="created_at")
    private Instant createdAt;
}
